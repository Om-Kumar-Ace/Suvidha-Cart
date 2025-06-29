import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { updateProfile } from 'firebase/auth';
import { auth } from '../firebase';
import { Box, Button, TextField, Typography, Avatar, Paper } from '@mui/material';
import { Email, Person } from '@mui/icons-material';

export default function ProfilePage() {
  const { currentUser } = useAuth();
  const [displayName, setDisplayName] = useState('');
  const [photoURL, setPhotoURL] = useState('');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (currentUser) {
      setDisplayName(currentUser.displayName || '');
      setPhotoURL(currentUser.photoURL || '');
      setEmail(currentUser.email || '');
    }
  }, [currentUser]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    
    try {
      await updateProfile(auth.currentUser, {
        displayName,
        photoURL: photoURL || null
      });
      setSuccess('Profile updated successfully!');
    } catch (err) {
      setError(err.message);
    }
    
    setLoading(false);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        Your Profile
      </Typography>
      
      <Paper sx={{ p: 3, maxWidth: 600, mx: 'auto' }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
          <Avatar
            src={photoURL}
            sx={{ width: 100, height: 100 }}
          >
            {displayName.charAt(0)}
          </Avatar>
        </Box>
        
        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}
        
        {success && (
          <Typography color="success.main" sx={{ mb: 2 }}>
            {success}
          </Typography>
        )}
        
        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            label="Name"
            fullWidth
            margin="normal"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            InputProps={{
              startAdornment: <Person sx={{ mr: 1 }} />,
            }}
          />
          
          <TextField
            label="Profile Photo URL"
            fullWidth
            margin="normal"
            value={photoURL}
            onChange={(e) => setPhotoURL(e.target.value)}
          />
          
          <TextField
            label="Email"
            fullWidth
            margin="normal"
            value={email}
            disabled
            InputProps={{
              startAdornment: <Email sx={{ mr: 1 }} />,
            }}
          />
          
          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            sx={{ mt: 2 }}
          >
            {loading ? 'Updating...' : 'Update Profile'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}