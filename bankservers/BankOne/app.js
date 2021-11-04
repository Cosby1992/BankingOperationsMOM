const express = require('express')
var amqp = require('amqplib/callback_api');

const sendAndRecieve = require('./sendAndRecieve');

const app = express()
const port = 3000

app.get('/', (req, res) => {
  res.send('Hello World!')
})

const args = process.argv.slice(2, process.argv.length);

sendAndRecieve.recieveMQMessage(args);

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})