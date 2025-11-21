package com.nhi.libary.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nhi.libary.model.Genre;
import com.nhi.libary.model.ListeningHistory;
import com.nhi.libary.model.Playlist;
import com.nhi.libary.model.SearchHistory;
import com.nhi.libary.model.Shareable;
import com.nhi.libary.model.Track;
import com.nhi.libary.model.TrackLibary;
import com.nhi.libary.model.User;
import com.nhi.libary.repository.GenreRepository;
import com.nhi.libary.repository.ListeningHistoryRepository;
import com.nhi.libary.repository.PlaylistRepository;
import com.nhi.libary.repository.SearchHistoryRepository;
import com.nhi.libary.repository.ShareableRepository;
import com.nhi.libary.repository.TrackLibaryRepository;
import com.nhi.libary.repository.TrackRepository;
import com.nhi.libary.repository.UserRepository;
import com.nhi.libary.util.ImageUpload;

@Service
public class TrackService {

	@Autowired
	private TrackRepository trackRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageUpload imageUpload;

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private ListeningHistoryRepository historyRepository;

	@Autowired
	private SearchHistoryRepository searchHistoryRepository;

	@Autowired
	private ShareableRepository shareableRepository;

	@Autowired
	private TrackLibaryRepository trackLibaryRepository;
	
	//thanh nhi thêm
	public Long getTotalTracks() {
	    return trackRepository.countAllTracks();
	}
	
	public List<Track> getTop5Tracks() {
        return trackRepository.findTop5ByOrderByNumberOfListensDesc();
    }
	

	public Track addTrack(Track track, MultipartFile imageOfTrack, MultipartFile fileOfTrack,
			Authentication authentication, int havePrivate, String cooperatorOfTrack) {
		try {
			Track checkDuplicateTrackTitle = this.trackRepository.findByTrackTitle(track.getTrackTitle());
			if (checkDuplicateTrackTitle != null) {
				if (track.getId() != null && checkDuplicateTrackTitle.getId() != track.getId()) {
					return null;
				} else if (track.getId() == null) {
					return null;
				}
			}

			Track trackFromDatabase = null;
			if (track.getId() != null) {
				trackFromDatabase = this.trackRepository.findById(track.getId()).get();
			}

			if (!imageOfTrack.isEmpty()) {
				track.setImage(Base64.getEncoder().encodeToString(imageOfTrack.getBytes()));
			} else {
				if (trackFromDatabase != null && trackFromDatabase.getImage() != null
						&& !trackFromDatabase.getImage().equals("")) {
					track.setImage(trackFromDatabase.getImage());
				}
			}

			if (!fileOfTrack.isEmpty()) {
				track.setTrackFile("file-of-track" + "/" + fileOfTrack.getOriginalFilename());
				imageUpload.uploadFileOfTrack(fileOfTrack);
			} else {
				if (trackFromDatabase != null && trackFromDatabase.getTrackFile() != null
						&& !trackFromDatabase.getTrackFile().equals("")) {
					track.setTrackFile(trackFromDatabase.getTrackFile());
				}
			}

			if (havePrivate == 1) {
				track.setPrivate(true);
			} else {
				track.setPrivate(false);
			}

			if (trackFromDatabase != null) {
				track.setReleaseDate(trackFromDatabase.getReleaseDate());
			} else {
				track.setReleaseDate(LocalDate.now());
			}

			if (trackFromDatabase != null) {
				track.setNumberOfListens(trackFromDatabase.getNumberOfListens());
			} else {
				track.setNumberOfListens(0);
			}

			User userUploadTrack = this.userRepository.findByEmail(authentication.getName());
			track.setUser(userUploadTrack);

			List<User> followingList = new ArrayList<>();

			if (cooperatorOfTrack != null && cooperatorOfTrack.equals("") == false) {
				for (String nameOfCooperatorUser : cooperatorOfTrack.split(",")) {
					if (nameOfCooperatorUser != null && nameOfCooperatorUser.equals("") == false) {
						User user = this.userRepository.findByEmail(nameOfCooperatorUser.trim());
						followingList.add(user);
					}
				}
				track.setUserList(followingList);
			} else {
				track.setUserList(null);
			}

			if (track.getLyrics() != null && track.getLyrics().equals("")) {
				track.setLyrics(null);
			} else if (track.getLyrics() == null) {
				track.setLyrics(null);
			}

			return this.trackRepository.save(track);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Track> findAllTrackOfArtist(Authentication authentication) {
		User user = this.userRepository.findByEmail(authentication.getName());
		return this.trackRepository.findByUser(user.getId());
	}

	public List<Track> findAllTrack() {
		return this.trackRepository.findAllTrackSortId();
	}

	public void deleteTrackById(Long idTrack) {
		Track track = this.trackRepository.findById(idTrack).get();
		List<Long> ids = this.playlistRepository.findPlaylistByTrack(track.getId());
		if (ids != null && !ids.isEmpty()) {

			for (Long id : ids) {
				Playlist playlist = this.playlistRepository.findById(id).get();
				
				List<Track> trackList = new ArrayList<>();
				List<Long> allTrackId= this.playlistRepository.findTrackListByPlaylistId(id);
				for (Long id_track : allTrackId) {
					if(id_track != track.getId()) {
						trackList.add(this.trackRepository.findById(id_track).get());
					}
				}
				
				playlist.setTrackList(trackList);
				this.playlistRepository.save(playlist);
			}

		}

		this.historyRepository.deleteAllByTrack(track);
		this.searchHistoryRepository.deleteAllByTrack(track);
		this.shareableRepository.deleteAllByTrack(track);
		this.trackLibaryRepository.deleteAllByTrack(track);

		this.trackRepository.deleteById(track.getId());
	}

	public Track findTrackById(Long id) {
		return this.trackRepository.findById(id).get();
	}

	public Track findTrackByTrackTitle(String trackTitle, Authentication authentication) {
		Track track = this.trackRepository.findByTrackTitle(trackTitle);
		if (track == null) {
			return null;
		} else if (!track.getUser().getEmail().equals(authentication.getName())) {
			return null;
		}

		return this.trackRepository.findByTrackTitle(trackTitle);
	}

	public Track deleteTrackByTrackTitle(String trackTitle, Authentication authentication) {
		Track track = this.trackRepository.findByTrackTitle(trackTitle);
		if (track == null) {
			return null;
		} else {
			List<Long> ids = this.playlistRepository.findPlaylistByTrack(track.getId());
			if (!track.getUser().getEmail().equals(authentication.getName())) {
				return null;
			} else if (ids != null && !ids.isEmpty()) {

				for (Long id : ids) {
					Playlist playlist = this.playlistRepository.findById(id).get();
					
					List<Track> trackList = new ArrayList<>();
					List<Long> allTrackId= this.playlistRepository.findTrackListByPlaylistId(id);
					for (Long id_track : allTrackId) {
						if(id_track != track.getId()) {
							trackList.add(this.trackRepository.findById(id_track).get());
						}
					}
					
					playlist.setTrackList(trackList);
					this.playlistRepository.save(playlist);
				}

			}

			this.historyRepository.deleteAllByTrack(track);
			this.searchHistoryRepository.deleteAllByTrack(track);
			this.shareableRepository.deleteAllByTrack(track);
			this.trackLibaryRepository.deleteAllByTrack(track);

			this.trackRepository.deleteById(track.getId());
			return track;
		}
	}

	public List<Track> findAllTrackGenreTogether(String trackTitle, String email) {
		Track track = this.trackRepository.findByTrackTitle(trackTitle);

//		sắp xếp cho thằng bấm vào nút play thì sẽ đứng đầu
		List<Track> trackList = new ArrayList<>();
		trackList.add(track);
		for (Track song : this.trackRepository.findByGenre(track.getGenre().getId())) {
			if ((song.getTrackTitle().equals(track.getTrackTitle()) == false && song.getUser().getEmail().equals(email)
					&& (song.isPrivate() == false || song.isPrivate() == true))
					|| (song.getTrackTitle().equals(track.getTrackTitle()) == false
							&& !song.getUser().getEmail().equals(email) && song.isPrivate() == false)) {
				trackList.add(song);
			}
		}
		return trackList;
	}

	public List<Track> findAllTrackGenreTogetherNotInTheQueue(String trackTitle, String email) {
		String[] allTrackTitle = trackTitle.split(", ");
		List<Long> trackList = new ArrayList<>();
		for (int i = 0; i < allTrackTitle.length; i++) {
			Track track = this.trackRepository.findByTrackTitle(allTrackTitle[i]);
			trackList.add(track.getId());
		}

		List<Track> allTrackNotInQueueAndGenreTogether = new ArrayList<>();

		Track track = this.trackRepository.findByTrackTitle(allTrackTitle[allTrackTitle.length - 1]);
		List<Track> allTrackNotInQueue = this.trackRepository.findByIdNotIn(trackList);
		for (Track song : allTrackNotInQueue) {
			if ((song.getGenre().getId() == track.getGenre().getId() && song.getUser().getEmail().equals(email)
					&& (song.isPrivate() == false || song.isPrivate() == true))
					|| (song.getGenre().getId() == track.getGenre().getId() && !song.getUser().getEmail().equals(email)
							&& song.isPrivate() == false)) {
				allTrackNotInQueueAndGenreTogether.add(song);
			}
		}

//		kiểm tra nếu thể loại ở phía trên không còn bài nào thì phát thể loại khác
		if (allTrackNotInQueueAndGenreTogether.isEmpty()) {
			Long genre = null;

			Optional<Genre> foundGenre = this.genreRepository.findById(track.getGenre().getId() + 1);
			if (!foundGenre.isPresent()) {
				genre = (long) 1;
			} else {
				genre = (long) track.getGenre().getId() + 1;
				List<Track> checkGenreNotHaveTracks = this.trackRepository.findByGenre(genre);
				boolean check = false;
				if (checkGenreNotHaveTracks == null || checkGenreNotHaveTracks.isEmpty()) {
					check = true;
				} else {
					for (Track t : checkGenreNotHaveTracks) {
						if ((t.getUser().getEmail().equals(email) && (t.isPrivate() == false || t.isPrivate() == true))
								|| (!t.getUser().getEmail().equals(email) && t.isPrivate() == false)) {
							check = false;
							break;
						} else {
							check = true;
						}
					}
				}

				if (check == true) {
					while (foundGenre.isPresent()) {
						genre++;
						foundGenre = this.genreRepository.findById(genre);
						checkGenreNotHaveTracks = this.trackRepository.findByGenre(genre);
						if (checkGenreNotHaveTracks != null && checkGenreNotHaveTracks.isEmpty() == false) {
							boolean isPrivate = true;
							for (Track t : checkGenreNotHaveTracks) {
								if ((t.getUser().getEmail().equals(email)
										&& (t.isPrivate() == false || t.isPrivate() == true))
										|| (!t.getUser().getEmail().equals(email) && t.isPrivate() == false)) {
									isPrivate = false;
									break;
								}
							}

							if (isPrivate == false) {
								break;
							}
						}
					}

					if (checkGenreNotHaveTracks == null || checkGenreNotHaveTracks.isEmpty()) {
						genre = (long) 1;
					}
				}
			}

			for (Track song : allTrackNotInQueue) {
				if ((song.getGenre().getId() == genre && song.getUser().getEmail().equals(email)
						&& (song.isPrivate() == false || song.isPrivate() == true))
						|| (song.getGenre().getId() == genre && !song.getUser().getEmail().equals(email)
								&& song.isPrivate() == false)) {
					allTrackNotInQueueAndGenreTogether.add(song);
				}
			}

//			nếu phát hết 4 thể loại rồi mà hết nhạc thì ta sẽ tìm lại tất cả bài hát 
			if (allTrackNotInQueueAndGenreTogether.isEmpty()) {
				allTrackNotInQueue = this.trackRepository.findAllTrackSortId();

				for (Track song : allTrackNotInQueue) {
					if ((song.getGenre().getId() == genre && song.getUser().getEmail().equals(email)
							&& (song.isPrivate() == false || song.isPrivate() == true))
							|| (song.getGenre().getId() == genre && !song.getUser().getEmail().equals(email)
									&& song.isPrivate() == false)) {
						allTrackNotInQueueAndGenreTogether.add(song);
					}
				}
			}
		}

		return allTrackNotInQueueAndGenreTogether;
	}

	public List<Track> findAllTrackByArtistName(String artistName) {
		User user = this.userRepository.findByArtistName(artistName);
		return this.trackRepository.findAllTrackByArtistName(user.getId());
	}

	public List<Track> findAllTrackByArtistNameInSeeAll(String artistName) {
		User user = this.userRepository.findByArtistName(artistName);
		return this.trackRepository.findAllTrackByArtistNameInSeeAll(user.getId());
	}

	public Track findByTrackTitleOfArtist(String trackTitle) {
		return this.trackRepository.findByTrackTitle(trackTitle);
	}

	public List<Track> findAllTrackByTrackTitleInSearch(String trackTitle) {
		return this.trackRepository.findAllTrackByTrackTitleInSearch(trackTitle);
	}

	public void increaseNumberOfListenByTrackTitle(String trackTitle) {
		this.trackRepository.increaseNumberOfListenByTrackTitle(trackTitle);
	}

	public boolean checkPrivate(String trackTitle) {
		Track track = this.trackRepository.findByTrackTitle(trackTitle);
		return track.isPrivate();
	}

	public String getLyrics(String trackTitle) throws IOException {
	    Track track = this.trackRepository.findByTrackTitle(trackTitle);

	    if (track == null) {
	        return "Không tìm thấy bài hát";
	    }

	    String trackName = track.getTrackTitle();
	    String artist = track.getLyrics(); 

	    if (artist == null || artist.trim().isEmpty()) {
	        return "Không có tên ca sĩ";
	    }

	    // Encode để tránh lỗi URL
	    trackName = URLEncoder.encode(trackName, "UTF-8");
	    artist = URLEncoder.encode(artist, "UTF-8");

	    // API của lyrics.ovh
	    URL url = new URL("https://api.lyrics.ovh/v1/" + artist + "/" + trackName);

	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");

	    int responseCode = conn.getResponseCode();

	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();

	        JSONObject json = new JSONObject(response.toString());

	        if (json.has("lyrics")) {
	            return json.getString("lyrics");
	        } else {
	            return "Không có lời";
	        }

	    } else {
	        return "Không có lời";
	    }
	}


}