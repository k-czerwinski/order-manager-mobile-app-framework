package pl.edu.agh.presentation.ui.common

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Product
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.ui.common.ProductEntry
import pl.edu.agh.framework.presentation.ui.common.ProductList
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.setPrivateField
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class ProductsTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            setPrivateField(UserRoleDependencyInjector, "userRoleParserInterface", UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productList_displaysProductEntries() {
        // arrange
        val products = listOf(
            Product(1, "Product 1", BigDecimal.valueOf(100.0), "Description for Product 1"),
            Product(2, "Product 2", BigDecimal(150.0), "Description for Product 2"),
            Product(3, "Product 3", BigDecimal(200.0), "Description for Product 3")
        )
        // act
        composeTestRule.setContent {
            ProductList(products = products)
        }
        // assert
        products.forEach { product ->
            composeTestRule.onNodeWithText(product.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(product.description).assertIsDisplayed()
            composeTestRule.onNodeWithText("Price: ${product.price}").assertIsDisplayed()
        }
    }

    @Test
    fun productList_showsNoProducts_whenListIsEmpty() {
        // arrange
        val emptyProducts = listOf<Product>()
        // act
        composeTestRule.setContent {
            ProductList(products = emptyProducts)
        }
        // assert
        composeTestRule.onNodeWithTag("productList").assertIsDisplayed()
            .assertContentDescriptionEquals()
    }

    @Test
    fun productEntry_displaysCorrectInformation_forProduct() {
        // arrange
        val product = Product(1, "Product 1", BigDecimal.valueOf(100.0), "Description for Product 1")
        // act
        composeTestRule.setContent {
            ProductEntry(product = product)
        }
        // assert
        composeTestRule.onNodeWithText(product.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(product.description).assertIsDisplayed()
        composeTestRule.onNodeWithText("Price: ${product.price}").assertIsDisplayed()
    }
}
