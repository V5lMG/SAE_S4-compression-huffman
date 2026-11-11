package fr.iutrodez.compresseurhuffman.huffman;

import fr.iutrodez.compresseurhuffman.objets.Noeud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompressionHuffmanTest {

    private CompressionHuffman compressionHuffmanTest;

    @BeforeEach
    void setUp() {
        compressionHuffmanTest = new CompressionHuffman("source.txt", "destination.txt");
    }

    @Test
    void testGetFrequenceDesOctets() {
        byte[] octets = { 'a', 'b', 'a', 'a', 'b', 'c' };
        Map<Byte, Integer> frequencies = compressionHuffmanTest.getFrequenceDesOctets(octets);

        assertEquals(3, frequencies.get((byte) 'a'));
        assertEquals(2, frequencies.get((byte) 'b'));
        assertEquals(1, frequencies.get((byte) 'c'));
    }

    @Test
    void testConstruireArbreAvecOccurences() {
        Map<Byte, Integer> occurrences = new HashMap<>();
        occurrences.put((byte) 'a', 5);
        occurrences.put((byte) 'b', 9);
        occurrences.put((byte) 'c', 12);
        occurrences.put((byte) 'd', 13);
        occurrences.put((byte) 'e', 16);
        occurrences.put((byte) 'f', 45);

        Noeud root = compressionHuffmanTest.construireArbreAvecOccurences(occurrences);

        assertNotNull(root);
        assertEquals(100, root.getFrequence());
    }

    @Test
    void testGenererCodesHuffman() {
        Map<Byte, Integer> occurrences = new HashMap<>();
        occurrences.put((byte) 'a', 5);
        occurrences.put((byte) 'b', 9);
        occurrences.put((byte) 'c', 12);
        occurrences.put((byte) 'd', 13);
        occurrences.put((byte) 'e', 16);
        occurrences.put((byte) 'f', 45);

        Map<Byte, String> codes = compressionHuffmanTest.genererCodesHuffman(occurrences);

        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        assertEquals(6, codes.size());
    }

    @Test
    void testCasSymboleUnique() {
        Map<Byte, Integer> occurrences = Collections.singletonMap((byte) 'z', 10);
        Map<Byte, String> codes = compressionHuffmanTest.genererCodesHuffman(occurrences);

        assertNotNull(codes);
        assertEquals("0", codes.get((byte) 'z'));
    }

    @Test
    void testCompressionEtDecompressionBoutEnBout(@TempDir Path tempDir) throws IOException {
        Path sourceFile = tempDir.resolve("source.txt");
        Path compressedFile = tempDir.resolve("archive.bin");
        Path decompressedFile = tempDir.resolve("restored.txt");

        String texteOriginal = "Bonjour, ceci est un test de compression et décompression de Huffman en version 2 !";
        Files.writeString(sourceFile, texteOriginal);

        CompressionHuffman compresseur = new CompressionHuffman(sourceFile.toString(), compressedFile.toString());
        compresseur.compresserFichier();

        assertTrue(Files.exists(compressedFile));
        assertTrue(Files.exists(tempDir.resolve("arbreHuffman.txt")));

        DecompressionHuffman decompresseur = new DecompressionHuffman(compressedFile.toString(), decompressedFile.toString());
        decompresseur.decompresserFichier();

        assertTrue(Files.exists(decompressedFile));
        assertEquals(texteOriginal, Files.readString(decompressedFile));
    }

    @Test
    void testCompressionFichierVide(@TempDir Path tempDir) throws IOException {
        Path sourceFile = tempDir.resolve("vide.txt");
        Path compressedFile = tempDir.resolve("vide.bin");
        Path decompressedFile = tempDir.resolve("vide_restored.txt");

        Files.write(sourceFile, new byte[0]);

        CompressionHuffman compresseur = new CompressionHuffman(sourceFile.toString(), compressedFile.toString());
        compresseur.compresserFichier();

        assertTrue(Files.exists(compressedFile));

        DecompressionHuffman decompresseur = new DecompressionHuffman(compressedFile.toString(), decompressedFile.toString());
        decompresseur.decompresserFichier();

        assertTrue(Files.exists(decompressedFile));
        assertEquals(0, Files.size(decompressedFile));
    }
}
