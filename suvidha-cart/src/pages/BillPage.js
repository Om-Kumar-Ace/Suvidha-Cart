// src/pages/BillPage.js
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useRef } from 'react';
import ReactToPrint from 'react-to-print';
import { 
  Box, 
  Button, 
  Typography, 
  Paper, 
  List, 
  ListItem, 
  ListItemText, 
  Divider 
} from '@mui/material';
import { ArrowBack, Print } from '@mui/icons-material';

const BillPage = () => {
  const { cart, storeInfo, clearCart } = useCart();
  const navigate = useNavigate();
  const billRef = useRef();

  // Calculate totals
  const subtotal = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  const tax = subtotal * 0.1; // 10% tax
  const total = subtotal + tax;

  const handleBackToHome = () => navigate('/home');
  const handleNewCart = () => {
    clearCart();
    navigate('/home');
  };

  if (!cart?.length) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Your cart is empty
        </Typography>
        <Button 
          variant="contained" 
          onClick={handleBackToHome}
          startIcon={<ArrowBack />}
        >
          Back to Home
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Button 
        startIcon={<ArrowBack />} 
        onClick={handleBackToHome} 
        sx={{ mb: 2 }}
        variant="outlined"
      >
        Back to Cart
      </Button>
      
      {/* Printable bill content */}
      <Box ref={billRef} sx={{ mb: 3 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h4" align="center" gutterBottom>
            {storeInfo?.name || 'Store Name'}
          </Typography>
          <Typography variant="subtitle1" align="center" gutterBottom>
            {storeInfo?.location || 'Store Location'}
          </Typography>
          <Typography variant="body2" align="center" gutterBottom color="text.secondary">
            {new Date().toLocaleString()}
          </Typography>
          
          <Divider sx={{ my: 3 }} />
          
          <List>
            {cart.map((item) => (
              <ListItem key={item.id} sx={{ px: 0 }}>
                <ListItemText
                  primary={item.name}
                  secondary={`${item.quantity} × $${item.price.toFixed(2)}`}
                  primaryTypographyProps={{ variant: 'body1' }}
                  secondaryTypographyProps={{ variant: 'body2' }}
                />
                <Typography variant="body1">
                  ${(item.price * item.quantity).toFixed(2)}
                </Typography>
              </ListItem>
            ))}
          </List>
          
          <Divider sx={{ my: 3 }} />
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="body1">Subtotal:</Typography>
            <Typography variant="body1">${subtotal.toFixed(2)}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="body1">Tax (10%):</Typography>
            <Typography variant="body1">${tax.toFixed(2)}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="body1">Discount:</Typography>
            <Typography variant="body1">$0.00</Typography>
          </Box>
          <Divider sx={{ my: 2 }} />
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="h6">Total:</Typography>
            <Typography variant="h6">${total.toFixed(2)}</Typography>
          </Box>
          
          <Typography variant="body2" align="center" sx={{ mt: 4 }} color="text.secondary">
            Thank you for your purchase!
          </Typography>
        </Paper>
      </Box>

      {/* Action buttons */}
      <Box sx={{ display: 'flex', gap: 2, mt: 3 }}>
        <ReactToPrint
          trigger={() => (
            <Button
              variant="contained"
              startIcon={<Print />}
              fullWidth
              size="large"
            >
              Print Receipt
            </Button>
          )}
          content={() => billRef.current}
        />
        
        <Button
          variant="outlined"
          onClick={handleNewCart}
          fullWidth
          size="large"
        >
          Start New Cart
        </Button>
      </Box>
    </Box>
  );
};

export default BillPage;