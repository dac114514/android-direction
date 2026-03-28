package com.example.backhome

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backhome.AddressItem
import com.example.backhome.R
import com.example.backhome.utils.MapNaviUtils
import com.example.backhome.ui.AddAddressActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var addButton: Button
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        initViews()
        loadAddresses()
        setupAddButton()
    }

    private fun initViews() {
        container = findViewById(R.id.addresses_container)
        addButton = findViewById(R.id.btn_add_address)
        emptyText = findViewById(R.id.tv_empty)
    }

    private fun setupAddButton() {
        addButton.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }
    }

    private fun loadAddresses() {
        val prefs = getSharedPreferences("addresses_data", MODE_PRIVATE)
        val addresses = mutableListOf<AddressItem>()

        // Get all saved addresses
        for (key in prefs.all.keys) {
            if (key.endsWith("_name")) {
                val id = key.removeSuffix("_name")
                val name = prefs.getString("${id}_name", "") ?: ""
                val address = prefs.getString("${id}_address", "") ?: ""

                if (!name.isEmpty() && !address.isEmpty()) {
                    addresses.add(AddressItem(id, name, address))
                }
            }
        }

        if (addresses.isEmpty()) {
            showEmptyState()
        } else {
            showAddresses(addresses)
        }
    }

    private fun showAddresses(addresses: List<AddressItem>) {
        container.removeAllViews()
        emptyText.visibility = TextView.GONE

        for (address in addresses) {
            val cardView = createAddressCard(address)
            container.addView(cardView)
        }
    }

    private fun createAddressCard(address: AddressItem): View {
        val cardView = layoutInflater.inflate(R.layout.item_address_card, container, false) as androidx.cardview.widget.CardView

        val tvName = cardView.findViewById<TextView>(R.id.tv_name)
        val tvAddress = cardView.findViewById<TextView>(R.id.tv_address)
        val btnNavigate = cardView.findViewById<Button>(R.id.btn_navigate)

        tvName.text = address.name
        tvAddress.text = address.address

        btnNavigate.setOnClickListener {
            navigateToAddress(address)
        }

        cardView.setOnLongClickListener {
            showAddressActions(address)
            true
        }

        return cardView
    }

    private fun showEmptyState() {
        container.removeAllViews()
        emptyText.visibility = TextView.VISIBLE
        emptyText.text = "暂无保存的地址\n点击下方按钮添加第一个地址"
    }

    private fun navigateToAddress(address: AddressItem) {
        // Use the improved MapNaviUtils for better Amap integration
        // For now, we'll use address name as destination since we don't have coordinates
        // In a real app, you'd want to geocode the address to get lat/lng

        // Try to use the address name as destination
        // 必须传“详细地址”而不是备注名
        val destinationAddress = address.address

        try {
            // 默认使用电动车导航
            MapNaviUtils.navigateToGaodeByAddress(
                context = this,
                destinationAddress = destinationAddress,
                destinationName = address.name.ifEmpty { "目的地" }
            )
        } catch (e: Exception) {
            openWebNavigation(address)
        }
    }

    private fun showAddressActions(address: AddressItem) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("地址操作")
        builder.setMessage("选择对\"${address.name}\"的操作")
        
        // 添加编辑选项
        builder.setPositiveButton("编辑") { _, _ ->
            editAddress(address)
        }
        
        // 添加删除选项
        builder.setNegativeButton("删除") { _, _ ->
            confirmDeleteAddress(address)
        }
        
        // 添加取消选项
        builder.setNeutralButton("取消", null)
        
        builder.show()
    }

    private fun editAddress(address: AddressItem) {
        val intent = Intent(this, AddAddressActivity::class.java).apply {
            putExtra("edit_mode", true)
            putExtra("item_id", address.id)
        }
        startActivityForResult(intent, ADD_ADDRESS_REQUEST)
    }

    private fun confirmDeleteAddress(address: AddressItem) {
        android.app.AlertDialog.Builder(this)
            .setTitle("删除地址")
            .setMessage("确认删除“${address.name}”吗？")
            .setPositiveButton("删除") { _, _ ->
                deleteAddress(address)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteAddress(address: AddressItem) {
        val prefs = getSharedPreferences("addresses_data", MODE_PRIVATE)
        prefs.edit().apply {
            remove("${address.id}_name")
            remove("${address.id}_address")
            apply()
        }
        loadAddresses()
        Toast.makeText(this, "已删除：${address.name}", Toast.LENGTH_SHORT).show()
    }

    private fun openWebNavigation(address: AddressItem) {
        // Simple fallback to web search
        try {
            val encodedAddress = Uri.encode(address.address)
            val webUri = Uri.parse("https://amap.com/search?query=$encodedAddress")
            val intent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "无法打开地图应用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_ADDRESS_REQUEST && resultCode == Activity.RESULT_OK) {
            val action = data?.getStringExtra("action")
            if (action == "added" || action == "updated") {
                loadAddresses() // Refresh the list
            }
        }
    }

    companion object {
        private const val ADD_ADDRESS_REQUEST = 1001
    }
}
