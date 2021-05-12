package learn.unexplained.data;

import learn.unexplained.models.Encounter;
import learn.unexplained.models.EncounterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EncounterFileRepositoryTest {

    static final String TEST_PATH = "./data/encounters-test.csv";
    static final String SEED_PATH = "./data/encounters-seed.csv";
    final Encounter[] testEncounters = new Encounter[]{
            new Encounter(1, EncounterType.UFO, "2020-01-01", "short test #1", 1),
            new Encounter(2, EncounterType.CREATURE, "2020-02-01", "short test #2", 1),
            new Encounter(3, EncounterType.SOUND, "2020-03-01", "short test #3", 1)
    };

    EncounterRepository repository = new EncounterFileRepository(TEST_PATH);

    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_PATH);
        Path testPath = Paths.get(TEST_PATH);

        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindAll() throws DataAccessException {
        List<Encounter> encounters = repository.findAll();
        Encounter[] actual = encounters.toArray(new Encounter[encounters.size()]);
        assertArrayEquals(testEncounters, actual);
    }

    @Test
    void shouldAdd() throws DataAccessException {
        Encounter encounter = new Encounter();
        encounter.setType(EncounterType.UFO);
        encounter.setWhen("Jan 15, 2005");
        encounter.setDescription("moving pinpoint of light." +
                "seemed to move with me along the highway. " +
                "then suddenly reversed direction without slowing down. it just reversed.");
        encounter.setOccurrences(1);

        Encounter actual = repository.add(encounter);

        assertNotNull(actual);
        assertEquals(4, actual.getEncounterId());
    }

    @Test
    void shouldFindEncounterByType() throws DataAccessException {
        //test the new findByType method
        List<Encounter> actual = repository.findByType(EncounterType.UFO);
        assertNotNull(actual);

        assertEquals(1, actual.size());
    }

    @Test
    void shouldUpdateFoundEncounter() throws DataAccessException {
        //TODO: test the new update method - found it case
        Encounter encounter = repository.findAll().get(2);
        encounter.setDescription("Updated Description Test");
        encounter.setOccurrences(3);
        assertTrue(repository.update(encounter));

        encounter = repository.findAll().get(2);
        assertNotNull(encounter);
        assertEquals("Updated Description Test", encounter.getDescription());
        assertEquals(3, encounter.getOccurrences());
    }

    @Test
    void shouldNotUpdateMissingEncounter() throws DataAccessException {
        //test the new update method - encounter doesn't exist case
        Encounter doesNotExist = new Encounter();
        doesNotExist.setEncounterId(1000);
        assertFalse(repository.update(doesNotExist));
    }

    @Test
    void shouldDeleteFoundEncounterById() throws DataAccessException {
        int count = repository.findAll().size();
        assertTrue(repository.deleteById(1));
        assertEquals(count - 1, repository.findAll().size());
    }

    @Test
    void shouldNotDeleteMissingEncounterById() throws DataAccessException {
       assertFalse(repository.deleteById(1024));
    }

}