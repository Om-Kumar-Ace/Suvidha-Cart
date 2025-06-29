import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, Container, Paper, Tab, Tabs, Typography } from '@mui/material';
import { LoginForm, RegisterForm } from '../components/Auth';
import { useTheme } from '../contexts/ThemeContext';

export default function AuthPage() {
  const { theme } = useTheme();
  const [tabValue, setTabValue] = useState(0);
  const navigate = useNavigate();

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4, background: theme.gradients.primary }}>
        <Typography variant="h4" align="center" color="white" gutterBottom>
          Suvidha Cart
        </Typography>
        <Typography variant="subtitle1" align="center" color="white" gutterBottom>
          Scan, Pay, Go - Faster Grocery Checkout
        </Typography>
      </Paper>
      
      <Paper elevation={3} sx={{ mt: 2, p: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} centered>
          <Tab label="Login" />
          <Tab label="Register" />
        </Tabs>
        
        <Box sx={{ mt: 3 }}>
          {tabValue === 0 ? <LoginForm /> : <RegisterForm />}
        </Box>
      </Paper>
      
      <Box sx={{ mt: 2, textAlign: 'center' }}>
        <Button variant="outlined" onClick={() => navigate('/')}>
          Continue as Guest
        </Button>
      </Box>
    </Container>
  );
}