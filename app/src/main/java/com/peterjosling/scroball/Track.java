package com.peterjosling.scroball;

import android.graphics.Bitmap;
import android.media.MediaMetadata;

import com.google.common.base.Optional;
import com.peterjosling.scroball.transforms.TitleExtractor;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Track {

  public abstract String track();
  public abstract String artist();
  public abstract Optional<String> album();
  public abstract Optional<String> albumArtist();
  public abstract Optional<Long> duration();
  public abstract Optional<Bitmap> art();

  public boolean isValid() {
    return !track().equals("") && !artist().equals("");
  }

  public static Track fromMediaMetadata(MediaMetadata metadata) {
    String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
    String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
    String album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
    String albumArtist = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
    Bitmap art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
    long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);

    if (title == null) {
      title = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);

      if (title  == null) {
        title = "";
      }
    }

    if (art == null) {
      art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
    }

    ImmutableTrack.Builder builder = ImmutableTrack.builder()
        .track(title)
        .art(Optional.fromNullable(art));

    if (duration > 0) {
      builder.duration(duration);
    }
    if (album != null && !album.isEmpty()) {
      builder.album(album);
    }
    if (albumArtist != null && !albumArtist.isEmpty()) {
      builder.albumArtist(albumArtist);
    }
    if (artist != null) {
      builder.artist(artist);
    } else {
      return new TitleExtractor().transform(builder.build());
    }
    return builder.build();
  }

  public boolean isSameTrack(Track track) {
    return track != null && track.track().equals(track()) && track.artist().equals(artist());
  }

  @Value.Lazy
  public static Track empty() {
    return ImmutableTrack.builder().track("").artist("").build();
  }
}
