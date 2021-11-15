const express = require('express')

const sendAndReceive = require('./sendAndReceive');
const {subscribeToLoanRequestsExchange} = require("./publishSubscribe");

const app = express()
const port = 3003

app.get('/', (req, res) => {
  res.send('Welcome to Evil Bank!');
})

//sendAndReceive.recieveMQMessage();
subscribeToLoanRequestsExchange();

app.listen(port, () => {
  console.log(`Always There For You is listening on port: ${port}`)
})