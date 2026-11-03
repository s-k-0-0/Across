package com.kax.across

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kax.across.data.local.AppDatabase
import com.kax.across.data.local.SavedItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {

    @Test
    fun database_deleteAll_test() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.savedItemDao()
        val id1 = dao.insert(SavedItem(url = "https://example.com/1", title = "Kotlin", description = "Desc 1"))
        dao.insert(SavedItem(url = "https://example.com/2", title = "Android", description = "Desc 2"))
        
        assertEquals(2, dao.getCount().first())

        // Test single item delete
        dao.deleteById(id1)
        assertEquals(1, dao.getCount().first())

        // Test getItemByUrl
        val urlItem = SavedItem(url = "https://x.com/AndroidDev", title = "X Post", description = "Desc")
        dao.insert(urlItem)
        val fetched = dao.getItemByUrl("https://x.com/AndroidDev")
        assertEquals("X Post", fetched?.title)

        // Test delete all
        dao.deleteAll()
        assertEquals(0, dao.getCount().first())

        db.close()
    }

    @Test
    fun sanity_check() {
        assertEquals(4, 2 + 2)
    }
}
