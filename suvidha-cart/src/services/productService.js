// Mock product database
const productDatabase = [
  {
    barcode: '123456789012',
    name: 'Premium Basmati Rice 5kg',
    price: 12.99,
    category: 'groceries',
    brand: 'India Gate'
  },
  {
    barcode: '234567890123',
    name: 'Amul Taaza Milk 1L',
    price: 3.49,
    category: 'dairy',
    brand: 'Amul'
  },
  {
    barcode: '345678901234',
    name: 'Britannia Whole Wheat Bread',
    price: 2.99,
    category: 'bakery',
    brand: 'Britannia'
  },
  {
    barcode: '456789012345',
    name: 'Dove Soap Bar 100g',
    price: 1.99,
    category: 'personal-care',
    brand: 'Dove'
  },
  {
    barcode: '567890123456',
    name: 'Maggi Noodles 70g',
    price: 0.99,
    category: 'instant-food',
    brand: 'Nestle'
  }
];

// Search products by name or barcode
export const searchProducts = async (query) => {
  if (!query) return [];
  
  const searchTerm = query.toLowerCase().trim();
  
  return productDatabase.filter(product => 
    product.name.toLowerCase().includes(searchTerm) ||
    product.barcode.includes(searchTerm)
  );
};

// Fetch product by barcode
export const fetchProductByBarcode = async (barcode) => {
  return productDatabase.find(product => product.barcode === barcode);
};

// Add new product to database (for admin use)
export const addProduct = async (productData) => {
  const newProduct = {
    barcode: productData.barcode,
    name: productData.name,
    price: parseFloat(productData.price),
    category: productData.category || 'uncategorized',
    brand: productData.brand || 'generic'
  };
  
  productDatabase.push(newProduct);
  return newProduct;
};

// Get all products (for debugging)
export const getAllProducts = async () => {
  return productDatabase;
};