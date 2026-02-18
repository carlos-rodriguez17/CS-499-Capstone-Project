const express = require('express');
const { MongoClient } = require('mongodb');
const bodyParser = require('body-parser');

const app = express();
const port = 4000;

// MongoDB connection (unchanged)
const url = 'mongodb://localhost:27017';
const dbName = 'customers';

let db;
let client;

app.use(bodyParser.json());
app.use(express.static('public'));
app.set('view engine', 'ejs');

// Connect to MongoDB
MongoClient.connect(url)
  .then(connectedClient => {
    client = connectedClient;
    db = client.db(dbName);
    console.log(`Connected to MongoDB database: ${dbName}`);
  })
  .catch(err => {
    console.error('MongoDB connection failed:', err);
    process.exit(1);
  });

// Root route - simple redirect to dashboard
app.get('/', (req, res) => {
  res.redirect('/dashboard');
});

// Status check
app.get('/status', (req, res) => {
  res.json({
    status: 'online',
    dbConnected: !!db,
    database: dbName,
    timestamp: new Date().toISOString()
  });
});

// GET all customers (API endpoint)
app.get('/customers', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected yet');
    const customers = await db.collection('customers').find({}).toArray();
    res.json(customers);
  } catch (err) {
    console.error('Error fetching customers:', err);
    res.status(500).json({ error: 'Failed to fetch customers', details: err.message });
  }
});

// GET one customer by customerNumber
app.get('/customers/:id', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected');
    const customer = await db.collection('customers').findOne({
      customerNumber: parseInt(req.params.id)
    });
    if (customer) {
      res.json(customer);
    } else {
      res.status(404).json({ error: 'Customer not found' });
    }
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch customer' });
  }
});

// POST - Add new customer
app.post('/customers', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected');
    const result = await db.collection('customers').insertOne(req.body);
    res.status(201).json({ 
      message: 'Customer added', 
      id: result.insertedId 
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to add customer' });
  }
});

// PUT - Update customer by customerNumber
app.put('/customers/:id', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected');
    const result = await db.collection('customers').updateOne(
      { customerNumber: parseInt(req.params.id) },
      { $set: req.body }
    );
    if (result.matchedCount > 0) {
      res.json({ message: 'Customer updated' });
    } else {
      res.status(404).json({ error: 'Customer not found' });
    }
  } catch (err) {
    res.status(500).json({ error: 'Failed to update customer' });
  }
});

// DELETE - Remove customer by customerNumber
app.delete('/customers/:id', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected');
    const result = await db.collection('customers').deleteOne({
      customerNumber: parseInt(req.params.id)
    });
    if (result.deletedCount > 0) {
      res.json({ message: 'Customer deleted' });
    } else {
      res.status(404).json({ error: 'Customer not found' });
    }
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete customer' });
  }
});

// ────────────────────────────────────────────────
// DASHBOARD - Modern HTML view for stakeholders
// ────────────────────────────────────────────────
app.get('/dashboard', async (req, res) => {
  try {
    if (!db) throw new Error('Database not connected');
    const customers = await db.collection('customers').find({}).toArray();
    res.render('dashboard', { customers });
  } catch (err) {
    console.error('Dashboard error:', err);
    res.status(500).send('Error loading dashboard');
  }
});

// Graceful shutdown
process.on('SIGINT', async () => {
  if (client) {
    await client.close();
    console.log('MongoDB connection closed');
  }
  process.exit(0);
});

app.listen(port, () => {
  console.log(`API server running at http://localhost:${port}`);
  console.log('Useful URLs:');
  console.log(`  - http://localhost:${port}/dashboard       → Modern dashboard`);
  console.log(`  - http://localhost:${port}/customers      → Raw JSON`);
  console.log(`  - http://localhost:${port}/status        → Connection status`);
});