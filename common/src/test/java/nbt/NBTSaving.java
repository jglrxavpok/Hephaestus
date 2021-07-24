package nbt;

import kotlin.Pair;
import org.jglrxavpok.hephaistos.nbt.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class NBTSaving {

    private ByteArrayOutputStream byteArrayOutputStream;

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveByte(CompressedMode compressedMode) throws IOException, NBTException {
        NBTByte nbt = NBT.Byte(42);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveShort(CompressedMode compressedMode) throws IOException, NBTException {
        NBTShort nbt = NBT.Short(1);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveInt(CompressedMode compressedMode) throws IOException, NBTException {
        NBTInt nbt = NBT.Int(0x42);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveLong(CompressedMode compressedMode) throws IOException, NBTException {
        NBTLong nbt = NBT.Long(0xCAFEBABEL);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveDouble(CompressedMode compressedMode) throws IOException, NBTException {
        NBTDouble nbt = NBT.Double(0.25);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveFloat(CompressedMode compressedMode) throws IOException, NBTException {
        NBTFloat nbt = NBT.Float(0.5f);
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveString(CompressedMode compressedMode) throws IOException, NBTException {
        NBTString nbt = NBT.String("AAA");
        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveList(CompressedMode compressedMode) throws IOException, NBTException {
        NBTList<NBTString> nbt = NBT.List(
                NBTTypes.TAG_String,
                NBT.String("A"), NBT.String("B"), NBT.String("C"), NBT.String("D")
        );

        test(nbt, compressedMode);
    }

    @ParameterizedTest
    @ArgumentsSource(CompressedModeProvider.class)
    public void saveCompound(CompressedMode compressedMode) throws IOException, NBTException {
        var compound = NBT.Compound((root) -> {
            root.put("byteArray", NBT.ByteArray(1, 2, 3));
            root.put("byte", NBT.Byte(0x42));
            root.put("double", NBT.Double(0.5));
            root.put("string", NBT.String("ABC"));
            root.put("float", NBT.Float(0.25f));
            root.put("int", NBT.Int(4567));
            root.put("intarray", NBT.IntArray(42, 42, 25464, 454, -10));
            root.put("long", NBT.Long(30000000000L));
            root.put("longarray", NBT.LongArray(30000000000L, -30000000000L, 130000000000L));
            root.put("short", NBT.Short(-10));
        });

        test(compound, compressedMode);
    }

    private <T extends NBT> void test(T nbt, CompressedMode compressedMode) throws IOException, NBTException {
        T saved = saveAndRead(nbt, compressedMode);
        assertEquals(nbt, saved);
    }


    private <T extends NBT> T saveAndRead(NBT tag, CompressedMode compressedMode) throws IOException, NBTException {
        NBTWriter output = output(compressedMode);
        output.writeNamed("a", tag);
        output.close();
        Pair<String, NBT> namedTag = input(compressedMode).readNamed();
        assertEquals("a", namedTag.getFirst());
        assertEquals(tag.getClass(), namedTag.getSecond().getClass());
        return (T) namedTag.getSecond();
    }


    private NBTReader input(CompressedMode compressedMode) {
        return NBTReader.fromArray(byteArrayOutputStream.toByteArray(), compressedMode);
    }

    private NBTWriter output(CompressedMode compressedMode) {
        return new NBTWriter(byteArrayOutputStream, compressedMode);
    }

    @BeforeEach
    public void init() {
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    public void clean() {
        byteArrayOutputStream = null;
    }


}
