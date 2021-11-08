const express = require('express')

const sendAndReceive = require('./sendAndReceive');

const app = express()
const port = 3003

app.get('/', (req, res) => {
  res.send('Welcome to Evil Bank!');
})

sendAndReceive.recieveMQMessage();

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})