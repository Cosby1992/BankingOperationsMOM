const amqp = require('amqplib/callback_api');
const bank_info = require("./bank_info.json");

const incoming_request_exchange_name = 'loan_request_exchange';
const response_exchange_name = 'loan_response_exchange';

function publishLoanResponse(json_string) {

    amqp.connect('amqp://localhost', function(error, connection) {
        if (error) {
            throw error;
        }
        connection.createChannel(function(error, channel) {
            if (error) {
                throw error;
            }

            channel.assertExchange(response_exchange_name, 'fanout', {
                durable: false
            });

            channel.publish(response_exchange_name, '', Buffer.from(json_string));

            console.log(" [x] Sent %s", json_string);
        });

        setTimeout(function() {
            connection.close();
        }, 500);

    });

}

function subscribeToLoanRequestsExchange () {

    amqp.connect('amqp://localhost', function(error, connection) {
        if (error) {
            throw error;
        }
        connection.createChannel(function(error, channel) {
            if (error) {
                throw error;
            }

            channel.assertExchange(incoming_request_exchange_name, 'fanout', {
                durable: false
            });

            channel.assertQueue('', {
                exclusive: true
            }, function(error, q) {
                if (error) {
                    throw error;
                }
                console.log(" [*] Waiting for messages in %s. To exit press CTRL+C", q.queue);
                channel.bindQueue(q.queue, incoming_request_exchange_name, '');

                channel.consume(q.queue, function(msg) {

                    console.log(" [x] Received %s", msg.content.toString());

                    const loanRequest = JSON.parse(msg.content.toString());

                    if(loanRequest.loanAmount < bank_info.loan_acceptance_interval[0] ||
                        loanRequest.loanAmount > bank_info.loan_acceptance_interval[1]){
                        return;
                    }

                    const interest = bank_info.loan_interest / 100.0;

                    const totalcost = loanRequest.loanAmount * Math.pow((1 + interest), loanRequest.paybackPeriod / 12 > 0 ? loanRequest.paybackPeriod / 12 : 1);
                    const costPrMonth = totalcost / loanRequest.paybackPeriod;

                    var response = {};

                    response.annualPercentageRate = String((interest * 100).toFixed(2));
                    response.totalCost = String(totalcost.toFixed(2));
                    response.costPerMonth = String(costPrMonth.toFixed(2));
                    response.paybackPeriod = String(loanRequest.paybackPeriod);
                    response.bankName = bank_info.bank_name;

                    publishLoanResponse(JSON.stringify(response));

                }, {
                    noAck: true
                });
            });
        });
    });

}

module.exports.publishLoanResponse = publishLoanResponse;
module.exports.subscribeToLoanRequestsExchange = subscribeToLoanRequestsExchange;