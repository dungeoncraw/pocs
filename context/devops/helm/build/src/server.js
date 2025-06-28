const express = require('express');
const app = express();
const port = process.env.PORT || 80;

app.get('/', (req, res) => {
    res.send('Hello from Australia!');
});

app.get('/health', (req, res) => {
    res.status(200).json({ status: 'healthy' });
});

app.listen(port, '0.0.0.0', () => {
    console.log(`App listening on port ${port}`);
});
