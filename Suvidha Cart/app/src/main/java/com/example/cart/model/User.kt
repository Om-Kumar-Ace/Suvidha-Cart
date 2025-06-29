package com.example.cart.model
import com.google.firebase.firestore.PropertyName
import android.os.Parcel
import android.os.Parcelable
data class User (
    val id: String = "", //ID User
    val name: String = "", //Nama User
    val phone: String = "", //No telp User
    val role: String = "user", //Role User (users/admin)
)
data class CartItem(
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = "",
    val productId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeDouble(price)
        parcel.writeInt(quantity)
        parcel.writeString(imageUrl)
        parcel.writeString(productId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem = CartItem(parcel)
        override fun newArray(size: Int): Array<CartItem?> = arrayOfNulls(size)
    }
}
data class Transaction(
    @PropertyName("totalAmount") val totalAmount: Double = 0.0,
    @PropertyName("paymentMethod") val paymentMethod: String = "",
    @PropertyName("timestamp") val timestamp: String = "",
    @PropertyName("items") val items: List<Map<String, Any>> = listOf()
)

