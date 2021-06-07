package org.javalaboratories.healthagents.controller;

import nl.altindag.log.LogCaptor;
import org.javalaboratories.healthagents.configuration.RsaSecureIdAuthenticationFilter;
import org.javalaboratories.healthagents.probes.HealthProbe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final HealthProbe mockHealthProbe = new HealthProbe() {
		@Override
		public String getName() {
			return "VPN Probe (Mock)";
		}
  		@Override
		public boolean detect() {
			return true;
		}
	};

	@Test
	public void testMainController_FtpHealth_Forbidden() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(get("https://localhost/api/agents/ftp/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getErrorLogs().stream()
				.anyMatch(s -> s.contains("RSA secure identifier invalid")));
	}

	@Test
	public void testMainController_RequestsHealth_Forbidden() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(get("https://localhost/api/agents/requests/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getErrorLogs().stream()
				.anyMatch(s -> s.contains("RSA secure identifier invalid")));
	}

	@Test
	public void testMainController_VpnHealth_Forbidden() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(get("https://localhost/api/agents/secure-traffic/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getErrorLogs().stream()
				.anyMatch(s -> s.contains("RSA secure identifier invalid")));
	}

	@Test
	@WithUserDetails("test") // ROLE_NONE
	public void testMainController_FtpHealthWitheTestUser_Forbidden() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/ftp/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getDebugLogs().stream()
				.anyMatch(s -> s.startsWith("RSA secure identifier received and verified successfully")));
	}

	@Test
	@WithUserDetails("test") // ROLE_NONE
	public void testMainController_VpnHealthWithTestUser_Forbidden() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/secure-traffic/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getDebugLogs().stream()
				.anyMatch(s -> s.startsWith("RSA secure identifier received and verified successfully")));
	}

	@Test
	@WithUserDetails("test") // ROLE_NONE
	public void testMainController_BadRsaToken_Forbidden() throws Exception {
		// Given
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get("https://localhost/api/agents/secure-traffic/health")
				.header("X-Header-RSA-Secure-ID","<bad-secure-rsa-token>");
		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(request
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		assertTrue(logCaptor.getErrorLogs().stream()
				.anyMatch(s -> s.contains("RSA secure identifier invalid")));
	}

	@Test
	@WithUserDetails("test") // ROLE_NONE
	public void testMainController_BadRsaTokenReportClientIp_Forbidden() throws Exception {
		// Given
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get("https://localhost/api/agents/secure-traffic/health")
				.header("X-Header-RSA-Secure-ID", "<bad-secure-rsa-token>")
				.header("HTTP_CLIENT_IP", "228.100.78.1")
				.header("HTTP_X_FORWARDED_FOR", "167.128.91.200; 99.128.100.1");

		// When
		LogCaptor logCaptor = LogCaptor.forClass(RsaSecureIdAuthenticationFilter.class);
		mockMvc.perform(request
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		// Then
		//
		// Expected log output is as follows :-
		// RSA secure identifier invalid, client details are as follows:
		//     --> HTTP_CLIENT_IP=228.100.78.1
		//     --> HTTP_X_FORWARDED_FOR=167.128.91.200; 99.128.100.1
		// Count the HTTP client IP address details, should be 2.
		assertEquals(2, Arrays.stream(logCaptor.getErrorLogs().get(0).split("\n"))
				.filter(s -> s.contains("HTTP_"))
				.mapToInt(s -> 1)
				.sum());
	}

	@Test
	@WithUserDetails("monitor") // ROLE_MONITOR
	public void testMainController_FtpHealth_Pass() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(MainController.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/ftp/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotImplemented());
		// Then
		assertTrue(logCaptor.getInfoLogs().stream()
				.anyMatch(s -> s.startsWith("Responding with 'Response[") && s.contains("it appears to be unstable or down")));
	}

	@Test
	@WithUserDetails("monitor") // ROLE_MONITOR
	public void testMainController_RequestsHealth_Pass() throws Exception {
		// Given
		System.setProperty("LOG_DIRECTORY","target/test-classes");

		// When
		LogCaptor logCaptor = LogCaptor.forClass(MainController.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/requests/health?alertTTL=1440")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError());
		// Then
		assertTrue(logCaptor.getInfoLogs().stream()
				.anyMatch(s -> s.startsWith("Responding with 'Response[") && s.contains("it appears to be unstable or down")));
	}

	@Test
	@WithUserDetails("monitor") // ROLE_MONITOR
	public void testMainController_VpnHealth_Pass() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(MainController.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/secure-traffic/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotImplemented());
		// Then
		assertTrue(logCaptor.getInfoLogs().stream()
				.anyMatch(s -> s.startsWith("Responding with 'Response[") && s.contains("it appears to be unstable or down")));
	}

	@Test
	@WithUserDetails("monitor") // ROLE_MONITOR
	public void testMainController_VpnHealth_Ok() throws Exception {
		// Given
		// When
		LogCaptor logCaptor = LogCaptor.forClass(MainController.class);
		mockMvc.perform(getWithCredentials("https://localhost/api/agents/service/health")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		// Then
		assertTrue(logCaptor.getInfoLogs().stream()
				.anyMatch(s -> s.startsWith("Responding with 'Response[") && s.contains("No additional information")));
	}

	private MockHttpServletRequestBuilder getWithCredentials(final String url) {
		return MockMvcRequestBuilders.get(url)
				.header("X-Header-RSA-Secure-ID", "c3111db326f6702bb0354dee2b8dafc9");
	}
}
