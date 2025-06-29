// src/components/ErrorBoundary.js
import { Typography, Button, Box } from '@mui/material';
import { useState } from 'react';

export default function ErrorBoundary({ children }) {
  const [hasError, setHasError] = useState(false);

  if (hasError) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h6" gutterBottom>
          Something went wrong
        </Typography>
        <Button 
          variant="contained" 
          onClick={() => window.location.reload()}
        >
          Reload Page
        </Button>
      </Box>
    );
  }

  try {
    return children;
  } catch (error) {
    console.error('Error caught:', error);
    setHasError(true);
    return null;
  }
}