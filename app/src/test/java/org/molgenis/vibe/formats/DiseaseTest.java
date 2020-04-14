package org.molgenis.vibe.formats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.molgenis.vibe.exceptions.InvalidStringFormatException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiseaseTest {
    @Test
    public void useValidIdWithLowercasePrefix() {
        Disease disease = new Disease("umls:C0123456");
        testIfValid(disease);
    }

    @Test
    public void useValidIdWithUppercasePrefix() {
        Disease disease = new Disease("UMLS:C0123456");
        testIfValid(disease);
    }

    @Test
    public void useValidIdWithSingleUpperCasePrefix1() {
        Assertions.assertThrows(InvalidStringFormatException.class, () -> new Disease("Umls:C0123456") );
    }

    @Test
    public void useValidIdWithSingleUpperCasePrefix2() {
        Assertions.assertThrows(InvalidStringFormatException.class, () -> new Disease("uMls:C0123456") );
    }

    @Test
    public void useValidIdWithInvalidPrefix() {
        Assertions.assertThrows(InvalidStringFormatException.class, () -> new Disease("ulms:C0123456") );
    }

    @Test
    public void useValidIdWithoutPrefix() {
        Assertions.assertThrows(InvalidStringFormatException.class, () -> new Disease("C0123456") );
    }

    @Test
    public void useUriAsIdInput() {
        Assertions.assertThrows(InvalidStringFormatException.class, () -> new Disease("http://linkedlifedata.com/resource/umls/id/C0123456") );
    }

    @Test
    public void useValidUri() {
        Disease disease = new Disease(URI.create("http://linkedlifedata.com/resource/umls/id/C0123456"));
        testIfValid(disease);
    }

    @Test
    public void useInvalidUri() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Disease(URI.create("http://linkedlifedata.com/resource/umls/C0123456")) );
    }

    @Test
    public void testSort() {
        List<Disease> actualOrder = new ArrayList<>( Arrays.asList(
                new Disease("umls:C0000020"),
                new Disease("umls:C0000003"),
                new Disease("umls:C0000008"),
                new Disease("umls:C0000001")
        ));

        List<Disease> expectedOrder = new ArrayList<>( Arrays.asList(
                actualOrder.get(3),
                actualOrder.get(1),
                actualOrder.get(2),
                actualOrder.get(0)
        ));

        Collections.sort(actualOrder);
        Assertions.assertEquals(expectedOrder, actualOrder);
    }

    private void testIfValid(Disease disease) {
        Assertions.assertAll(
                () -> Assertions.assertEquals("C0123456", disease.getId()),
                () -> Assertions.assertEquals("umls:C0123456", disease.getFormattedId()),
                () -> Assertions.assertEquals(URI.create("http://linkedlifedata.com/resource/umls/id/C0123456"), disease.getUri())
        );
    }

    @Test
    public void testEqualsIdToEqualId() {
        Assertions.assertTrue(new Disease("umls:C0123456").equals(new Disease("umls:C0123456")));
    }

    @Test
    public void testEqualsUriToEqualUri() {
        Assertions.assertTrue(new Disease(URI.create("http://linkedlifedata.com/resource/umls/id/C0123456")).equals(new Disease(URI.create("http://linkedlifedata.com/resource/umls/id/C0123456"))));
    }

    @Test
    public void testEqualsIdToEqualUri() {
        Assertions.assertTrue(new Disease("umls:C0123456").equals(new Disease(URI.create("http://linkedlifedata.com/resource/umls/id/C0123456"))));
    }

    @Test
    public void testEqualsIdToDifferentId() {
        Assertions.assertFalse(new Disease("umls:C0123456").equals(new Disease("umls:C9874565")));
    }
}
