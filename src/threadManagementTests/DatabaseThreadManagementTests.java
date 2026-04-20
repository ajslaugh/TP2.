package threadManagementTests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import database.Database;
import entityClasses.ThreadType;

/*******
 * <p> Title: DatabaseThreadManagementTests Class </p>
 * 
 * <p> Description: This class contains JUnit tests for the TP3 thread
 * management database methods. These tests validate thread creation,
 * retrieval, update, archive, deletion, and status behaviors. </p>
 * 
 * <p> Tests Covered: </p>
 * <ul>
 *   <li><b>THREAD-01:</b> Create Thread: Verifies that a new thread can be added with valid input.</li>
 *   <li><b>THREAD-02:</b> Update Thread: Verifies that an existing thread’s name and description can be updated.</li>
 *   <li><b>THREAD-03:</b> Delete Thread: Verifies that a thread can be removed from the database by ID.</li>
 *   <li><b>THREAD-04:</b> Archive Thread: Verifies that a thread can be marked as archived.</li>
 *   <li><b>THREAD-05:</b> Retrieve Threads: Verifies that all threads can be retrieved.</li>
 *   <li><b>THREAD-06:</b> Active Thread Filter: Verifies that only active threads are returned for posting.</li>
 *   <li><b>THREAD-07:</b> Thread Status Check: Verifies that archived threads are correctly identified as inactive.</li>
 * </ul>
 * 
 * <p> These tests validate the database methods for thread management to verify
 * correct system behavior. </p>
 * 
 * @author Gabriella Romero
 */
public class DatabaseThreadManagementTests {

    private Database db;

    /*****
     * <p> Method: setUp() </p>
     * 
     * <p> Description: Creates a fresh database reference and connects to the
     * database before each test. </p>
     */
    @Before
    public void setUp() throws SQLException {
        db = new Database();
        db.connectToDatabase();
    }

    /*****
     * <p> Method: testAddThreadType() </p>
     * 
     * <p> Description: Verifies that a new thread can be added to the database. </p>
     * 
     * <p> Test: THREAD-01 Create Thread </p>
     */
    @Test
    public void testAddThreadType() {
        String threadName = "JUnit Thread " + System.currentTimeMillis();
        boolean result = db.addThreadType(threadName, "Created by test");
        assertTrue(result);
    }

    /*****
     * <p> Method: testGetAllThreadsDetailed() </p>
     * 
     * <p> Description: Verifies that the detailed thread list can be retrieved
     * and contains thread records. </p>
     * 
     * <p> Test: THREAD-05 Retrieve Threads </p>
     */
    @Test
    public void testGetAllThreadsDetailed() {
        String threadName = "Detailed Thread " + System.currentTimeMillis();
        db.addThreadType(threadName, "List retrieval test");

        List<ThreadType> threads = db.getAllThreadsDetailed();

        assertNotNull(threads);
        assertFalse(threads.isEmpty());
    }

    /*****
     * <p> Method: testGetActiveThreadTypes() </p>
     * 
     * <p> Description: Verifies that active thread names are returned. </p>
     * 
     * <p> Test: THREAD-06 Active Thread Filter </p>
     */
    @Test
    public void testGetActiveThreadTypes() {
        String threadName = "Active Thread " + System.currentTimeMillis();
        db.addThreadType(threadName, "Active list test");

        List<String> activeThreads = db.getActiveThreadTypes();

        assertNotNull(activeThreads);
        assertTrue(activeThreads.contains(threadName));
    }

    /*****
     * <p> Method: testUpdateThreadType() </p>
     * 
     * <p> Description: Verifies that an existing thread can be updated. </p>
     * 
     * <p> Test: THREAD-02 Update Thread </p>
     */
    @Test
    public void testUpdateThreadType() {
        String originalName = "Old Name " + System.currentTimeMillis();
        db.addThreadType(originalName, "Old Description");

        List<ThreadType> threads = db.getAllThreadsDetailed();
        ThreadType target = null;

        for (ThreadType t : threads) {
            if (originalName.equals(t.getName())) {
                target = t;
                break;
            }
        }

        assertNotNull(target);

        boolean updated = db.updateThreadType(
            target.getId(),
            "New Name " + System.currentTimeMillis(),
            "New Description",
            "active"
        );

        assertTrue(updated);
    }

    /*****
     * <p> Method: testArchiveThreadType() </p>
     * 
     * <p> Description: Verifies that a thread can be archived and is no longer
     * considered active. </p>
     * 
     * <p> Test: THREAD-04 Archive Thread </p>
     */
    @Test
    public void testArchiveThreadType() {
        String threadName = "Archive Me " + System.currentTimeMillis();
        db.addThreadType(threadName, "Archive test");

        List<ThreadType> threads = db.getAllThreadsDetailed();
        ThreadType target = null;

        for (ThreadType t : threads) {
            if (threadName.equals(t.getName())) {
                target = t;
                break;
            }
        }

        assertNotNull(target);

        boolean archived = db.archiveThreadType(target.getId());
        assertTrue(archived);

        boolean isActive = db.isThreadActive(threadName);
        assertFalse(isActive);
    }

    /*****
     * <p> Method: testRemoveThreadTypeById() </p>
     * 
     * <p> Description: Verifies that a thread can be deleted by ID. </p>
     * 
     * <p> Test: THREAD-03 Delete Thread </p>
     */
    @Test
    public void testRemoveThreadTypeById() {
        String threadName = "Delete Me " + System.currentTimeMillis();
        db.addThreadType(threadName, "Delete test");

        List<ThreadType> threads = db.getAllThreadsDetailed();
        ThreadType target = null;

        for (ThreadType t : threads) {
            if (threadName.equals(t.getName())) {
                target = t;
                break;
            }
        }

        assertNotNull(target);

        boolean deleted = db.removeThreadTypeById(target.getId());
        assertTrue(deleted);
    }

    /*****
     * <p> Method: testIsThreadActive() </p>
     * 
     * <p> Description: Verifies that active status is correctly reported for
     * a normal thread. </p>
     * 
     * <p> Test: THREAD-07 Thread Status Check </p>
     */
    @Test
    public void testIsThreadActive() {
        String threadName = "Status Check Thread " + System.currentTimeMillis();
        db.addThreadType(threadName, "Status test");
        assertTrue(db.isThreadActive(threadName));
    }
}