import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import { useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, IconButton, Avatar, Box } from '@mui/material';
import { Brightness4, Brightness7, Home, History, Person, ShoppingCart } from '@mui/icons-material';

export default function Header() {
  const { currentUser } = useAuth();
  const { theme, toggleTheme, isDarkMode } = useTheme();
  const navigate = useNavigate();

  return (
    <AppBar position="static" sx={{ background: theme.gradients.primary }}>
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Suvidha Cart
        </Typography>
        
        {currentUser && (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton color="inherit" onClick={() => navigate('/home')}>
              <Home />
            </IconButton>
            
            <IconButton color="inherit" onClick={() => navigate('/history')}>
              <History />
            </IconButton>
            
            <IconButton color="inherit" onClick={() => navigate('/profile')}>
              {currentUser.photoURL ? (
                <Avatar src={currentUser.photoURL} sx={{ width: 24, height: 24 }} />
              ) : (
                <Person />
              )}
            </IconButton>
          </Box>
        )}
        
        <IconButton color="inherit" onClick={toggleTheme} sx={{ ml: 1 }}>
          {isDarkMode ? <Brightness7 /> : <Brightness4 />}
        </IconButton>
      </Toolbar>
    </AppBar>
  );
}