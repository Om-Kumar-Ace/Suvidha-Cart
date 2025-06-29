import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";


const firebaseConfig = {
  apiKey: "AIzaSyDR6sNmmFz2Hawf4Td7Ssy2HYHRCbAcH7w",
  authDomain: "cartmycart.firebaseapp.com",
  projectId: "cartmycart",
  storageBucket: "cartmycart.firebasestorage.app",
  messagingSenderId: "799015607484",
  appId: "1:799015607484:web:0ad68709bbfb55a59084b9",
  measurementId: "G-YDR60CSD1P"
}
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);