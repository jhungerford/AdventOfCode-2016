package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem4Test {

  @Test
  public void commonLetters() {
    Problem4.Room room = Problem4.Room.fromLine("aaaaa-bbb-z-y-x-123[abxyz]");

    assertThat(room.encryptedName).isEqualTo("aaaaa-bbb-z-y-x");
    assertThat(room.sector).isEqualTo(123);
    assertThat(room.checksum).isEqualTo("abxyz");

    assertThat(room.isValid()).isTrue();
  }

  @Test
  public void alphabeticChecksum() {
    Problem4.Room room = Problem4.Room.fromLine("a-b-c-d-e-f-g-h-987[abcde]");

    assertThat(room.encryptedName).isEqualTo("a-b-c-d-e-f-g-h");
    assertThat(room.sector).isEqualTo(987);
    assertThat(room.checksum).isEqualTo("abcde");

    assertThat(room.isValid()).isTrue();
  }

  @Test
  public void mixedLetters() {
    Problem4.Room room = Problem4.Room.fromLine("not-a-real-room-404[oarel]");

    assertThat(room.encryptedName).isEqualTo("not-a-real-room");
    assertThat(room.sector).isEqualTo(404);
    assertThat(room.checksum).isEqualTo("oarel");

    assertThat(room.isValid()).isTrue();
  }

  @Test
  public void invalidRoom() {
    Problem4.Room room = Problem4.Room.fromLine("totally-real-room-200[decoy]");

    assertThat(room.encryptedName).isEqualTo("totally-real-room");
    assertThat(room.sector).isEqualTo(200);
    assertThat(room.checksum).isEqualTo("decoy");

    assertThat(room.isValid()).isFalse();
  }

  @Test
  public void sumValidRooms() {
    int validSectorSum = Problem4.sumValidRoomSectors(ImmutableList.of(
        "aaaaa-bbb-z-y-x-123[abxyz]",
        "a-b-c-d-e-f-g-h-987[abcde]",
        "not-a-real-room-404[oarel]",
        "totally-real-room-200[decoy]"
    ));

    assertThat(validSectorSum).isEqualTo(1514);
  }

  @Test
  public void decrypt() {
    Problem4.Room room = Problem4.Room.fromLine("qzmt-zixmtkozy-ivhz-343[zimth]");

    assertThat(room.isValid()).isTrue();
    assertThat(room.decryptedName()).isEqualTo("very encrypted name");
  }
}
