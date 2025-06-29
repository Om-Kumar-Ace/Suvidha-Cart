import { db } from '../firebase';
import { collection, addDoc, query, where, getDocs, orderBy } from 'firebase/firestore';

// Save cart to user's purchase history
export const saveCartToHistory = async (cartData) => {
  try {
    const docRef = await addDoc(collection(db, 'purchaseHistory'), {
      ...cartData,
      createdAt: new Date().toISOString()
    });
    return { success: true, id: docRef.id };
  } catch (error) {
    console.error("Error saving to history: ", error);
    return { success: false, error };
  }
};

// Get user's purchase history
export const getUserPurchaseHistory = async (userId) => {
  try {
    const q = query(
      collection(db, 'purchaseHistory'),
      where('userId', '==', userId),
      orderBy('createdAt', 'desc')
    );
    
    const querySnapshot = await getDocs(q);
    return querySnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
  } catch (error) {
    console.error("Error fetching history: ", error);
    return [];
  }
};

// Get single purchase by ID
export const getPurchaseById = async (purchaseId) => {
  try {
    const q = query(
      collection(db, 'purchaseHistory'),
      where('id', '==', purchaseId)
    );
    
    const querySnapshot = await getDocs(q);
    if (querySnapshot.empty) return null;
    
    return {
      id: querySnapshot.docs[0].id,
      ...querySnapshot.docs[0].data()
    };
  } catch (error) {
    console.error("Error fetching purchase: ", error);
    return null;
  }
};