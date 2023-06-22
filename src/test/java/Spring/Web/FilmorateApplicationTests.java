package spring.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import spring.web.adapters.DurationAdapter;
import spring.web.model.Film;
import spring.web.model.User;
import spring.web.adapters.LocalDateAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {
	URI uri;
	String url = "http://localhost:8080";
	HttpRequest request;
	Gson gson = getGson();

	public static Gson getGson() {
		Gson gson = new Gson();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
		gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
		gson = gsonBuilder.create();
		return gson;
	}

	@Test
	void shouldCreateAndUpdateFilmWithoutException() throws IOException, InterruptedException {
		//POST request
		final Film film = Film.builder()
				.name("FirstFilmTrain")
				.description("The train is following you. Everywhere.")
				.releaseDate(LocalDate.of(2021, 03, 14))
				.duration(Duration.ofMinutes(120))
				.build();
		String jsonFilm = gson.toJson(film);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		uri = URI.create(url + "/films");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(200, response.statusCode());
		response = httpClient.send(request, handler);
		assertEquals(404, response.statusCode());
		//PUT request
		final Film filmUpdate = Film.builder()
				.id(1)
				.name("FirstFilmTrain")
				.description("The train is following you. Everywhere.")
				.releaseDate(LocalDate.of(2021, 03, 14))
				.duration(Duration.ofMinutes(120))
				.build();
		jsonFilm = gson.toJson(filmUpdate);
		body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		request = HttpRequest.newBuilder()
				.PUT(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		handler = HttpResponse.BodyHandlers.ofString();
		response = httpClient.send(request, handler);
		assertEquals(200, response.statusCode());
		//GET request
		request = HttpRequest.newBuilder()
				.GET()
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
		assertEquals(jsonFilm, response.body().substring(1, response.body().length() - 1));
	}

	@Test
	void shouldCreateFilmWithoutName() throws IOException, InterruptedException {
		final Film film = Film.builder()
				.name("")
				.description("The train is following you. Everywhere.")
				.releaseDate(LocalDate.of(2021, 03, 14))
				.duration(Duration.ofMinutes(120))
				.build();
		String jsonFilm = gson.toJson(film);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		uri = URI.create(url + "/films");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateFilmWhenDescriptionMore200Characters() throws IOException, InterruptedException {
		final Film film = Film.builder()
				.name("New")
				.description("The train is following you. Everywhere. The moment you stumble, he will be very near. " +
						"Don't turn around, get up and walk on. There's nothing you can do anyway. " +
						"Don't waste your time on fear. Go ahead and buy a 3/4-foot burger.")
				.releaseDate(LocalDate.of(2021, 03, 14))
				.duration(Duration.ofMinutes(120))
				.build();
		String jsonFilm = gson.toJson(film);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		uri = URI.create(url + "/films");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateFilmWhenDateEarlier1895() throws IOException, InterruptedException {
		final Film film = Film.builder()
				.name("New")
				.description("Some description")
				.releaseDate(LocalDate.of(1895, 12, 27))
				.duration(Duration.ofMinutes(120))
				.build();
		String jsonFilm = gson.toJson(film);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		uri = URI.create(url + "/films");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateFilmWhenDurationNegativeOrZero() throws IOException, InterruptedException {
		final Film filmNegativeDuration = Film.builder()
				.name("New")
				.description("Some description")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(Duration.ofMinutes(-1))
				.build();
		String jsonFilm = gson.toJson(filmNegativeDuration);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		uri = URI.create(url + "/films");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
		final Film filmZeroDuration = Film.builder()
				.name("New")
				.description("Some description")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(Duration.ofMinutes(-1))
				.build();
		jsonFilm = gson.toJson(filmZeroDuration);
		httpClient = HttpClient.newHttpClient();
		body = HttpRequest.BodyPublishers.ofString(jsonFilm);
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		handler = HttpResponse.BodyHandlers.ofString();
		response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateAndUpdateUserWithoutException() throws IOException, InterruptedException {
		//POST request
		final User user = User.builder()
				.email("first@user.com")
				.login("first")
				.name("FirstName")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		String jsonUser = gson.toJson(user);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(200, response.statusCode());
		//PUT request
		final User userUpdate = User.builder()
				.id(1)
				.email("update@user.com")
				.login("LoginUpdate")
				.name("NameUpdate")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		jsonUser = gson.toJson(userUpdate);
		body = HttpRequest.BodyPublishers.ofString(jsonUser);
		request = HttpRequest.newBuilder()
				.PUT(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		handler = HttpResponse.BodyHandlers.ofString();
		response = httpClient.send(request, handler);
		assertEquals(200, response.statusCode());
		//GET request
		request = HttpRequest.newBuilder()
				.GET()
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
		assertEquals(userUpdate, gson.fromJson(response.body().substring(1, response.body().length() - 1), User.class));
	}

	@Test
	void shouldCreateUserWithWrongEmail() throws IOException, InterruptedException {
		final User user = User.builder()
				.email("firstuser.com")
				.login("first")
				.name("FirstName")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		String jsonUser = gson.toJson(user);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateUserWithEmptyEmail() throws IOException, InterruptedException {
		final User user = User.builder()
				.email("")
				.login("first")
				.name("FirstName")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		String jsonUser = gson.toJson(user);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateUserWithWrongLogin() throws IOException, InterruptedException {
		//Try to create a user with empty login
		final User userEmptyLogin = User.builder()
				.email("email@ya.ru")
				.login("")
				.name("FirstName")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		String jsonUser = gson.toJson(userEmptyLogin);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
		//Try to create a user with space in the login
		final User userLoginWithSpace = User.builder()
				.email("email@ya.ru")
				.login("Login space")
				.name("FirstName")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		jsonUser = gson.toJson(userLoginWithSpace);
		httpClient = HttpClient.newHttpClient();
		body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		handler = HttpResponse.BodyHandlers.ofString();
		response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldCreateUserWithEmptyName() throws IOException, InterruptedException {
		final User user = User.builder()
				.email("first@user.com")
				.login("first")
				.name("")
				.birthday(LocalDate.of(2021, 03, 14))
				.build();
		String jsonUser = gson.toJson(user);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(200, response.statusCode());
		User userResponse = gson.fromJson(response.body(), User.class);
		assertEquals("first", userResponse.getName());
	}

	@Test
	void shouldCreateUserFromFuture() throws IOException, InterruptedException {
		final User user = User.builder()
				.email("first@user.com")
				.login("first")
				.name("")
				.birthday(LocalDate.of(2024, 03, 14))
				.build();
		String jsonUser = gson.toJson(user);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonUser);
		uri = URI.create(url + "/users");
		request = HttpRequest.newBuilder()
				.POST(body)
				.uri(uri)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = httpClient.send(request, handler);
		assertEquals(400, response.statusCode());
	}
}
