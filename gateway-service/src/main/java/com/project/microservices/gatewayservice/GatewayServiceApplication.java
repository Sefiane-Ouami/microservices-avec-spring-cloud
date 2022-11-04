package com.project.microservices.gatewayservice;

import com.netflix.discovery.DiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	//@Bean
//	RouteLocator staticRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(r->r.path("/customers/**").uri("http://localhost:8081/"))
//				.route(r->r.path("/products/**").uri("http://localhost:8082/"))
//				.build();
//		return builder.routes()
//				.route(r->r.path("/customers/**").uri("lb://CUSTOMER-SERVICE"))
//				.route(r->r.path("/products/**").uri("lb://INVENTORY-SERVICE"))
//				.build();
//	}

	@Bean
	DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp) {
		return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
	}

	@Bean
	RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r
						.path("/weather/**")
						.filters(f -> f
								.addRequestParameter("lat","35.5888995")
								.addRequestParameter("lon","-5.3625516")
								.addRequestHeader("X-RapidAPI-Host", "weatherbit-v1-mashape.p.rapidapi.com")
								.addRequestHeader("X-RapidAPI-Key", "a493391027mshd8dbcb2657d008cp153bfdjsnd87c6f2d5930")
								.rewritePath("/weather/(?<segment>.*)", "/${segment}")
								.circuitBreaker(h->h.setName("weather").setFallbackUri("forward:/defaultWeather"))
						)
						.uri("https://weatherbit-v1-mashape.p.rapidapi.com")
				)
				.route(r -> r
						.path("/muslimsalat/**")
						.filters(f -> f
								.addRequestHeader("X-RapidAPI-Host", "muslimsalat.p.rapidapi.com")
								.addRequestHeader("X-RapidAPI-Key", "a493391027mshd8dbcb2657d008cp153bfdjsnd87c6f2d5930")
								.rewritePath("/muslimsalat/(?<segment>.*)", "/${segment}")
								.circuitBreaker(h->h.setName("muslimsalat").setFallbackUri("forward:/defaultSalat"))
						)
						.uri("https://muslimsalat.p.rapidapi.com")
				)
				.route(r -> r
						.path("/LiveScore/**")
						.filters(f -> f
								.addRequestParameter("Category","soccer")
								.addRequestParameter("Timezone","+1")
								.addRequestHeader("X-RapidAPI-Host", "livescore6.p.rapidapi.com")
								.addRequestHeader("X-RapidAPI-Key", "a493391027mshd8dbcb2657d008cp153bfdjsnd87c6f2d5930")
								.rewritePath("/LiveScore/(?<segment>.*)", "/${segment}")
								.circuitBreaker(h->h.setName("LiveScore").setFallbackUri("forward:/defaultLiveScore"))
						)
						.uri("https://livescore6.p.rapidapi.com")
				)
				.build();
	}

	@RestController
	class CircuitBreakerRestController {
		@GetMapping("/defaultWeather")
		public Map<String,String> weather() {
			Map<String,String> data = new HashMap<>();
			data.put("message", "default weather");
			data.put("weather", "25, 26, 22, ...");
			return data;
		}
		@GetMapping("/defaultSalat")
		public Map<String,String> salat() {
			Map<String,String> data = new HashMap<>();
			data.put("Message", "Horaire Salat en Tetouan");
			data.put("Fajr", "06:12");
			data.put("Shouruk", "07:38");
			data.put("Dohr", "13:10");
			data.put("Asr", "16:03");
			data.put("Maghreb", "18:32");
			data.put("Ishaa", "19:48");
			return data;
		}
		@GetMapping("/defaultLiveScore")
		public Map<String,String> LiveScore() {
			Map<String,String> data = new HashMap<>();
			data.put("Message", "No Live Matches Available");
			return data;
		}
	}

}
