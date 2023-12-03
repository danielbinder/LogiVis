import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Hashing.sha512;

public class HashingTest {
    @Test
    public void testHelloWorld() {
        assertEquals("3d58a719c6866b0214f96b0a67b37e51a91e233ce0be126a08f35fdf4c043c6126f40139bfbc338d44eb2a03de9f7bb8eff0ac260b3629811e389a5fbee8a894",
                     sha512("Hello World"));

        assertEquals("1ff3b9a5a901fe7ea94ea089e49e6d215ee34bdbabb6b78241b373b2a29b0bbbaf6b17280d619079bd8ef0ebe384414ba39886560cb13fa03bb1192613265df8",
                     sha512("hello World"));

        assertEquals("e2e1c9e522efb2495a178434c8bb8f11000ca23f1fd679058b7d7e141f0cf3433f94fc427ec0b9bebb12f327a3240021053db6091196576d5e6d9bd8fac71c0c",
                     sha512("Hello world"));

        assertEquals("840006653e9ac9e95117a15c915caab81662918e925de9e004f774ff82d7079a40d4d27b1b372657c61d46d470304c88c788b3a4527ad074d1dccbee5dbaa99a",
                     sha512("hello world"));
    }
}
