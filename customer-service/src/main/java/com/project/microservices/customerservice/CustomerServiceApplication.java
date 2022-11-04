package com.project.microservices.customerservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	private String Name;
	private String email;
}

@RepositoryRestResource
interface CustomerRepository extends JpaRepository<Customer,Long>{}

@Projection(name = "projection1", types = Customer.class)
interface CustomerProjection {
	public String getName();
	public String getEmail();
}
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}
	
	@Bean
	CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration repositoryRestConfiguration) {
		return args -> {
			repositoryRestConfiguration.exposeIdsFor(Customer.class);
			customerRepository.save(new Customer(null,"Faouzi","faouzi.seddouki@etu.uae.ac.ma"));
			customerRepository.save(new Customer(null,"Sefiane","sefiane.ouami@etu.uae.ac.ma"));
			customerRepository.findAll().forEach(System.out::println);
		};
	}

}
