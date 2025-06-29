import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { collection, query, where, getDocs } from 'firebase/firestore';
import { db } from '../firebase';
import { Box, Typography, Paper, List, ListItem, ListItemText, Divider, Button } from '@mui/material';
import { History as HistoryIcon } from '@mui/icons-material';

export default function HistoryPage() {
  const { currentUser } = useAuth();
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBills = async () => {
      if (currentUser) {
        try {
          const q = query(
            collection(db, 'bills'),
            where('userId', '==', currentUser.uid)
          );
          
          const querySnapshot = await getDocs(q);
          const billsData = [];
          
          querySnapshot.forEach((doc) => {
            billsData.push({ id: doc.id, ...doc.data() });
          });
          
          setBills(billsData);
        } catch (err) {
          console.error('Error fetching bills:', err);
        } finally {
          setLoading(false);
        }
      }
    };
    
    fetchBills();
  }, [currentUser]);

  if (loading) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography>Loading...</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <HistoryIcon sx={{ mr: 1 }} />
        Purchase History
      </Typography>
      
      {bills.length === 0 ? (
        <Typography sx={{ mt: 2 }}>
          No purchase history found.
        </Typography>
      ) : (
        <List>
          {bills.map((bill) => (
            <Paper key={bill.id} sx={{ mb: 2 }}>
              <ListItem>
                <ListItemText
                  primary={bill.storeInfo.name}
                  secondary={new Date(bill.timestamp?.toDate()).toLocaleString()}
                />
                <Typography variant="subtitle1">
                  ${bill.total.toFixed(2)}
                </Typography>
              </ListItem>
              
              <Divider />
              
              <Box sx={{ p: 2 }}>
                <Typography variant="body2" gutterBottom>
                  Items: {bill.items.length}
                </Typography>
                <Button size="small" variant="outlined">
                  View Details
                </Button>
              </Box>
            </Paper>
          ))}
        </List>
      )}
    </Box>
  );
}