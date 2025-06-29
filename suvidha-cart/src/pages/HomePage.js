import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import { Box, Button, TextField, Typography, Paper, List, ListItem, ListItemText, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, Autocomplete, Chip, Divider } from '@mui/material';
import { Add, Remove, Delete, CameraAlt, Store, Discount, Receipt } from '@mui/icons-material';
import BarcodeScanner from '../components/BarcodeScanner/BarcodeScanner';
import { searchProducts, fetchProductByBarcode } from '../services/productService';
import { saveCartToHistory } from '../services/historyService';

export default function HomePage() {
  const { currentUser } = useAuth();
  const { cart, storeInfo, setStoreInfo, addItem, removeItem, updateItem, clearCart } = useCart();
  const [storeName, setStoreName] = useState(storeInfo?.name || '');
  const [storeLocation, setStoreLocation] = useState(storeInfo?.location || '');
  const [showScanner, setShowScanner] = useState(false);
  const [manualMode, setManualMode] = useState(false);
  const [manualItem, setManualItem] = useState({ 
    name: '', 
    price: 0, 
    quantity: 1, 
    discount: 0,
    barcode: ''
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [openDiscountDialog, setOpenDiscountDialog] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const navigate = useNavigate();

  // Search products when query changes (with debounce)
  useEffect(() => {
    const timer = setTimeout(() => {
      if (searchQuery.trim().length > 2) {
        searchProducts(searchQuery).then(results => {
          setSearchResults(results);
        });
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  const handleSaveStore = () => {
    setStoreInfo({ 
      name: storeName, 
      location: storeLocation,
      timestamp: new Date().toISOString()
    });
  };

  const handleScan = async (barcode) => {
    try {
      const product = await fetchProductByBarcode(barcode);
      if (product) {
        addItem({
          id: `${barcode}-${Date.now()}`,
          barcode: barcode,
          ...product,
          quantity: 1,
          discount: 0,
          originalPrice: product.price
        });
      } else {
        // If product not found in database, allow manual entry with pre-filled barcode
        setManualItem(prev => ({
          ...prev,
          barcode: barcode,
          name: `Product ${barcode.substring(0, 6)}`
        }));
        setManualMode(true);
      }
    } catch (error) {
      console.error('Error fetching product:', error);
      // Fallback to manual entry
      setManualItem(prev => ({
        ...prev,
        barcode: barcode,
        name: `Product ${barcode.substring(0, 6)}`
      }));
      setManualMode(true);
    } finally {
      setShowScanner(false);
    }
  };

  const handleManualAdd = () => {
    addItem({
      id: manualItem.barcode ? `${manualItem.barcode}-${Date.now()}` : Date.now(),
      name: manualItem.name,
      price: parseFloat(manualItem.price),
      quantity: manualItem.quantity,
      discount: parseFloat(manualItem.discount) || 0,
      originalPrice: parseFloat(manualItem.price),
      barcode: manualItem.barcode || null
    });
    
    setManualItem({ name: '', price: 0, quantity: 1, discount: 0, barcode: '' });
    setManualMode(false);
    setSearchQuery('');
  };

  const handleQuantityChange = (id, change) => {
    const item = cart.find(item => item.id === id);
    if (item) {
      const newQuantity = item.quantity + change;
      if (newQuantity > 0) {
        updateItem(id, { quantity: newQuantity });
      }
    }
  };

  const handleGenerateBill = async () => {
    if (cart.length > 0) {
      try {
        // Save current cart to history before navigating
        if (currentUser) {
          await saveCartToHistory({
            userId: currentUser.uid,
            items: cart,
            storeInfo,
            subtotal: calculateSubtotal(),
            tax: calculateTax(),
            discount: calculateTotalDiscount(),
            total: calculateTotal(),
            timestamp: new Date().toISOString()
          });
        }
        navigate('/bill');
      } catch (error) {
        console.error('Error saving cart to history:', error);
        // Still navigate to bill page even if history save fails
        navigate('/bill');
      }
    }
  };

  const handleOpenDiscountDialog = (item) => {
    setSelectedItem(item);
    setOpenDiscountDialog(true);
  };

  const handleApplyDiscount = (discountValue) => {
    if (selectedItem) {
      updateItem(selectedItem.id, { 
        discount: parseFloat(discountValue),
        price: selectedItem.originalPrice * (1 - parseFloat(discountValue)/100)
      });
    }
    setOpenDiscountDialog(false);
  };

  // Calculation functions
  const calculateSubtotal = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const calculateTax = () => {
    return calculateSubtotal() * 0.1; // 10% tax
  };

  const calculateTotalDiscount = () => {
    return cart.reduce((total, item) => {
      if (item.discount) {
        return total + (item.originalPrice * item.quantity * item.discount/100);
      }
      return total;
    }, 0);
  };

  const calculateTotal = () => {
    return calculateSubtotal() + calculateTax();
  };

  if (!storeInfo) {
    return (
      <Box sx={{ p: 3, maxWidth: 600, mx: 'auto' }}>
        <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
          <Store sx={{ mr: 1 }} />
          Enter Store Details
        </Typography>
        <Paper sx={{ p: 3, mb: 3 }}>
          <TextField
            label="Store Name"
            fullWidth
            margin="normal"
            value={storeName}
            onChange={(e) => setStoreName(e.target.value)}
            required
          />
          <TextField
            label="Store Location"
            fullWidth
            margin="normal"
            value={storeLocation}
            onChange={(e) => setStoreLocation(e.target.value)}
            required
          />
          <Button
            variant="contained"
            onClick={handleSaveStore}
            disabled={!storeName || !storeLocation}
            sx={{ mt: 2 }}
            fullWidth
            size="large"
          >
            Start Shopping
          </Button>
        </Paper>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 2, maxWidth: 800, mx: 'auto' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6" sx={{ display: 'flex', alignItems: 'center' }}>
          <Store sx={{ mr: 1 }} />
          {storeInfo.name}
          <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>
            {storeInfo.location}
          </Typography>
        </Typography>
        <Button 
          variant="outlined" 
          size="small"
          onClick={() => {
            clearCart();
            setStoreInfo(null);
          }}
        >
          Change Store
        </Button>
      </Box>
      
      {/* Product Search and Add */}
      <Box sx={{ mb: 3 }}>
        <Autocomplete
          freeSolo
          options={searchResults}
          getOptionLabel={(option) => option.name || option}
          inputValue={searchQuery}
          onInputChange={(e, newValue) => setSearchQuery(newValue)}
          onChange={(e, newValue) => {
            if (newValue && typeof newValue !== 'string') {
              addItem({
                id: `${newValue.barcode}-${Date.now()}`,
                ...newValue,
                quantity: 1,
                discount: 0,
                originalPrice: newValue.price
              });
              setSearchQuery('');
            }
          }}
          renderInput={(params) => (
            <TextField 
              {...params} 
              label="Search products..." 
              fullWidth 
            />
          )}
          renderOption={(props, option) => (
            <li {...props}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                <Typography>{option.name}</Typography>
                <Typography color="text.secondary">${option.price.toFixed(2)}</Typography>
              </Box>
            </li>
          )}
        />
        
        <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
          <Button
            variant="contained"
            startIcon={<CameraAlt />}
            onClick={() => setShowScanner(true)}
            sx={{ flex: 1 }}
          >
            Scan Barcode
          </Button>
          
          <Button
            variant="outlined"
            onClick={() => setManualMode(true)}
            sx={{ flex: 1 }}
          >
            Add Manually
          </Button>
        </Box>
      </Box>

      {/* Barcode Scanner Dialog */}
      {showScanner && (
        <BarcodeScanner 
          onScan={handleScan} 
          onClose={() => setShowScanner(false)} 
        />
      )}

      {/* Manual Add Dialog */}
      {manualMode && (
        <Dialog open={manualMode} onClose={() => setManualMode(false)} fullWidth maxWidth="sm">
          <DialogTitle>Add Product Manually</DialogTitle>
          <DialogContent>
            <TextField
              label="Barcode (optional)"
              fullWidth
              margin="normal"
              value={manualItem.barcode}
              onChange={(e) => setManualItem({...manualItem, barcode: e.target.value})}
            />
            <TextField
              label="Product Name"
              fullWidth
              margin="normal"
              value={manualItem.name}
              onChange={(e) => setManualItem({...manualItem, name: e.target.value})}
              required
            />
            <TextField
              label="Price"
              type="number"
              fullWidth
              margin="normal"
              value={manualItem.price}
              onChange={(e) => setManualItem({...manualItem, price: e.target.value})}
              inputProps={{ min: 0, step: 0.01 }}
              required
            />
            <TextField
              label="Quantity"
              type="number"
              fullWidth
              margin="normal"
              value={manualItem.quantity}
              onChange={(e) => setManualItem({...manualItem, quantity: parseInt(e.target.value) || 1})}
              inputProps={{ min: 1 }}
              required
            />
            <TextField
              label="Discount (%)"
              type="number"
              fullWidth
              margin="normal"
              value={manualItem.discount}
              onChange={(e) => setManualItem({...manualItem, discount: e.target.value})}
              inputProps={{ min: 0, max: 100, step: 1 }}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setManualMode(false)}>Cancel</Button>
            <Button 
              onClick={handleManualAdd}
              variant="contained"
              disabled={!manualItem.name || !manualItem.price}
            >
              Add Product
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Discount Dialog */}
      <Dialog open={openDiscountDialog} onClose={() => setOpenDiscountDialog(false)}>
        <DialogTitle>Apply Discount</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Apply discount to {selectedItem?.name}
          </Typography>
          <TextField
            autoFocus
            margin="dense"
            label="Discount Percentage"
            type="number"
            fullWidth
            variant="standard"
            inputProps={{ min: 0, max: 100, step: 1 }}
            onChange={(e) => {
              const value = Math.min(100, Math.max(0, e.target.value));
              handleApplyDiscount(value);
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDiscountDialog(false)}>Cancel</Button>
          <Button onClick={() => handleApplyDiscount(0)}>Remove Discount</Button>
        </DialogActions>
      </Dialog>

      {/* Cart Items */}
      {cart.length > 0 ? (
        <>
          <List sx={{ mb: 2 }}>
            {cart.map((item) => (
              <Paper key={item.id} sx={{ mb: 1 }}>
                <ListItem
                  secondaryAction={
                    <IconButton edge="end" onClick={() => removeItem(item.id)}>
                      <Delete />
                    </IconButton>
                  }
                >
                  <ListItemText
                    primary={item.name}
                    secondary={
                      <>
                        {item.barcode && <span>Barcode: {item.barcode}<br /></span>}
                        ${item.price.toFixed(2)} x {item.quantity}
                        {item.discount > 0 && (
                          <Chip 
                            label={`${item.discount}% off`} 
                            size="small" 
                            color="success" 
                            sx={{ ml: 1 }}
                            icon={<Discount fontSize="small" />}
                            onClick={() => handleOpenDiscountDialog(item)}
                          />
                        )}
                      </>
                    }
                  />
                  <Box sx={{ display: 'flex', alignItems: 'center', ml: 2 }}>
                    <IconButton 
                      onClick={() => handleQuantityChange(item.id, -1)}
                      size="small"
                    >
                      <Remove fontSize="small" />
                    </IconButton>
                    <Typography sx={{ mx: 1 }}>{item.quantity}</Typography>
                    <IconButton 
                      onClick={() => handleQuantityChange(item.id, 1)}
                      size="small"
                    >
                      <Add fontSize="small" />
                    </IconButton>
                  </Box>
                </ListItem>
                {item.discount > 0 && (
                  <Box sx={{ px: 2, pb: 1, textAlign: 'right' }}>
                    <Typography variant="body2" color="text.secondary">
                      You save: ${(item.originalPrice * item.quantity * item.discount/100).toFixed(2)}
                    </Typography>
                  </Box>
                )}
              </Paper>
            ))}
          </List>

          {/* Order Summary */}
          <Paper sx={{ p: 2, mb: 2 }}>
            <Typography variant="subtitle1" gutterBottom>
              Order Summary
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography>Subtotal:</Typography>
              <Typography>${calculateSubtotal().toFixed(2)}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography>Discount:</Typography>
              <Typography color="success.main">
                -${calculateTotalDiscount().toFixed(2)}
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography>Tax (10%):</Typography>
              <Typography>${calculateTax().toFixed(2)}</Typography>
            </Box>
            <Divider sx={{ my: 1 }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between', fontWeight: 'bold' }}>
              <Typography>Total:</Typography>
              <Typography>${calculateTotal().toFixed(2)}</Typography>
            </Box>
          </Paper>

          <Button
            variant="contained"
            fullWidth
            size="large"
            onClick={handleGenerateBill}
            startIcon={<Receipt />}
            sx={{ mb: 2 }}
          >
            Generate Bill
          </Button>
        </>
      ) : (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <CameraAlt sx={{ fontSize: 60, color: 'action.active', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            Your cart is empty
          </Typography>
          <Typography color="text.secondary">
            Scan barcodes or search products to add items to your cart
          </Typography>
        </Paper>
      )}
    </Box>
  );
}