const express = require('express')

const {subscribeToLoanRequestsExchange} = require("./publishSubscribe");

const app = express()
const port = 3002

app.get('/', (req, res) => {
  res.send('Welcome to Evil Bank!');
})

subscribeToLoanRequestsExchange();

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})