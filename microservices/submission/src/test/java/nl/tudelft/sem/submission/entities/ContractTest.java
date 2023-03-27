package nl.tudelft.sem.submission.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContractTest {

    private static final Contract contract = new Contract();
    private static final Contract contract2 = new Contract();

    @BeforeEach
    void setUp() {
        contract.setCourseCode("CSE1200");
        contract.setBucketKey("BK1");
        contract.setStudentNumber(101010L);

        contract2.setCourseCode("CSE1200");
        contract2.setBucketKey("BK1");
        contract2.setStudentNumber(101010L);
    }

    @Test
    void testGetCourseCode() {
        contract.setCourseCode("CSE2510");
        assertEquals("CSE2510", contract.getCourseCode());
    }

    @Test
    void testGetBucketKey() {
        contract.setCourseCode("BK42");
        assertEquals("BK42", contract.getCourseCode());
    }

    @Test
    void testGetStudentNumber() {
        contract.setStudentNumber(111111L);
        assertEquals(111111L, contract.getStudentNumber());
    }

    @Test
    void testEqualsToSelf() {
        assertEquals(contract, contract);
    }

    @Test
    void testEqualsAllFields() {
        assertEquals(contract2, contract);
    }

    @Test
    void testNotEqualBucketKey() {
        contract2.setBucketKey("BK2");
        assertNotEquals(contract2, contract);
    }

    @Test
    void testNotEqualCourseCode() {
        contract2.setCourseCode("CSE2510");
        assertNotEquals(contract2, contract);
    }

    @Test
    void testNotEqualStudentNumber() {
        contract2.setStudentNumber(contract.getStudentNumber() + 42L);
        assertNotEquals(contract2, contract);
    }

    @Test
    void testNotEqualAllFields() {
        contract2.setBucketKey("BK2");
        contract2.setCourseCode("CSE2520");
        contract2.setStudentNumber(contract.getStudentNumber() + 42L);
        assertNotEquals(contract2, contract);
    }

    @Test
    void testNotEqualNull() {
        assertNotEquals(null, contract);
    }

    @Test
    void testNotEqualDifferentClass() {
        assertNotEquals("Contract", contract);
    }

    @Test
    void testToString() {
        assertEquals("Contract(bucketKey=BK1, studentNumber=101010"
                + ", courseCode=CSE1200)", contract.toString());
    }


    @Test
    void hashCodeTest() {
        int hash = Objects.hash(contract.getBucketKey(),
                contract.getStudentNumber(),
                contract.getCourseCode());
        assertEquals(hash, contract.hashCode());
    }

}
