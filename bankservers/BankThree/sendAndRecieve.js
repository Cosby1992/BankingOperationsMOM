var amqp = require('amqplib/callback_api');

module.exports.recieveMQMessage = function recieveMQMessage(args) {

    amqp.connect('amqp://localhost', function(error, connection) {
    if (error) {
        throw error;
    }

    console.log(args);

    connection.createChannel(function(error, channel) {
        if (error) {
            throw error;
        }

        var queue = 'LoanRequest';

        channel.assertQueue(queue, {
            durable: false
        });

        console.log(" [*] Waiting for messages in %s. To exit press CTRL+C", queue);

        channel.consume(queue, function(msg) {

            const loanRequest = JSON.parse(msg.content.toString());
            const interest = Number(args[0]) / 100;

            const totalcost = loanRequest.loanAmount * Math.pow((1 + interest), loanRequest.paybackPeriod % 12);
            const costPrMonth = totalcost / loanRequest.paybackPeriod;

            var bankname = args.slice(1, args.length);

            var response = {};

            response.apr = String(interest * 100);
            response.totalcost = String(totalcost);
            response.costPrMonth = String(costPrMonth);
            response.paybackPeriod = String(loanRequest.paybackPeriod);
            response.bankname = bankname;

            sendMQMessage(JSON.stringify(response));

            console.log(" [x] Received %s", msg.content.toString());
        }, {
            noAck: true
        });
    });
});
    
}

function sendMQMessage(message) {
    
    amqp.connect('amqp://localhost', function(error, connection) {
    if (error) {
        throw error;
    }

    connection.createChannel(function(error, channel) {
        if (error) {
            throw error;
        }

        var queue = 'LoanResponse';

        channel.assertQueue(queue, {
            durable: false
        });
        channel.sendToQueue(queue, Buffer.from(message));

        console.log(" [x] Sent %s", message);
    });

    setTimeout(function() {
        connection.close();
    }, 500);
});
}
