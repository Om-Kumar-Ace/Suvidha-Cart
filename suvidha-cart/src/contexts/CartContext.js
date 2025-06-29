import { createContext, useContext, useState } from 'react';

const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);
  const [storeInfo, setStoreInfo] = useState(null);

  const addItem = (item) => {
    setCart([...cart, item]);
  };

  const removeItem = (id) => {
    setCart(cart.filter(item => item.id !== id));
  };

  const updateItem = (id, updates) => {
    setCart(cart.map(item => item.id === id ? { ...item, ...updates } : item));
  };

  const clearCart = () => {
    setCart([]);
    setStoreInfo(null);
  };

  return (
    <CartContext.Provider value={{ cart, storeInfo, addItem, removeItem, updateItem, setStoreInfo, clearCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}