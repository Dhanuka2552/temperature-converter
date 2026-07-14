const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.SPRING_API_URL || 'http://localhost:8081';
const SPRING_API_KEY = process.env.SPRING_API_KEY || 'SUPER-SECRET-DEV-KEY-2552';

// Serve static files (index.html, style.css, script.js)
app.use(express.static(__dirname));
app.use(express.json());

// Convert: forwards value/from/to to the Spring backend, which saves history too
app.post('/api/convert', async (req, res) => {
    const { value, from, to } = req.query;

    try {
        const response = await fetch(
            `${BACKEND_URL}/api/temperatures/convert?value=${value}&from=${from}&to=${to}`,
            { method: 'POST', headers: { 'X-API-KEY': SPRING_API_KEY } }
        );

        const data = await response.json();
        if (!response.ok) return res.status(response.status).json(data);
        res.json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// History
app.get('/api/history', async (req, res) => {
    try {
        const response = await fetch(`${BACKEND_URL}/api/temperatures/history`, {
            headers: { 'X-API-KEY': SPRING_API_KEY }
        });
        const data = await response.json();
        res.status(response.status).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Clear history
app.delete('/api/history', async (req, res) => {
    try {
        const response = await fetch(`${BACKEND_URL}/api/temperatures/history`, {
            method: 'DELETE',
            headers: { 'X-API-KEY': SPRING_API_KEY }
        });
        const data = await response.json();
        res.status(response.status).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Stats
app.get('/api/stats', async (req, res) => {
    try {
        const response = await fetch(`${BACKEND_URL}/api/temperatures/stats`, {
            headers: { 'X-API-KEY': SPRING_API_KEY }
        });
        const data = await response.json();
        res.status(response.status).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Safety / warning check
async function proxyWarningCheck(req, res) {
    const { value, unit } = req.query;

    if (value === undefined || unit === undefined) {
        return res.status(400).json({ error: 'value and unit are required query params' });
    }

    try {
        const response = await fetch(
            `${BACKEND_URL}/api/temperatures/warning-check?value=${value}&unit=${unit}`,
            { headers: { 'X-API-KEY': SPRING_API_KEY } }
        );

        const message = await response.text();
        const isWarning = message.toLowerCase().startsWith('warning:');
        return res.status(response.status).json({ message, warning: isWarning });
    } catch (error) {
        return res.status(500).json({ error: error.message });
    }
}
app.get('/warning-check', proxyWarningCheck);

app.listen(PORT, () => {
    console.log(`Server running and accessible at: http://localhost:${PORT}`);
});
