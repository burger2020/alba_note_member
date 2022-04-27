import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
	kotlin("kapt") version "1.6.10"
}

noArg {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
	annotation("com.albanote.memberservice.NoArgAndAllOpen")
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
	annotation("com.albanote.memberservice.NoArgAndAllOpen")
}

group = "com.albanote"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2021.0.0"

dependencies {
	//spring
	implementation("org.springframework.kafka:spring-kafka")  // kafka
	implementation("redis.clients:jedis:4.2.1") //redis

	//spring boot
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator") // use actuator / config yml 파일 변경사항 반영
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	//query dsl
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
	implementation("com.querydsl:querydsl-jpa")
	kapt("com.querydsl:querydsl-apt:5.0.0:jpa")

	runtimeOnly("org.postgresql:postgresql")
	implementation("io.jsonwebtoken:jjwt:0.9.1")

	//aws
	implementation("org.springframework.cloud:spring-cloud-aws:2.2.6.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-aws-context:2.2.6.RELEASE")
	// aws java sdk
	implementation(platform("com.amazonaws:aws-java-sdk-bom:1.11.997"))
	implementation("com.amazonaws:aws-java-sdk-cloudfront")

//	// firebase
//	implementation("com.google.firebase:firebase-admin:8.0.0")
//
//	//ect
//	implementation(group= "net.logstash.logback", name = "logstash-logback-encoder", version = "6.6")
//	runtimeOnly("org.postgresql:postgresql")
//	implementation("io.jsonwebtoken:jjwt:0.9.1")
//
//	//aws
//	implementation("org.springframework.cloud:spring-cloud-aws:2.2.5.RELEASE")
//	implementation("org.springframework.cloud:spring-cloud-aws-context:2.2.5.RELEASE")
//	// aws java sdk
//	implementation(platform("com.amazonaws:aws-java-sdk-bom:1.11.997"))
//	implementation("com.amazonaws:aws-java-sdk-cloudfront")
//
//	//jsp
//	implementation("javax.servlet:jstl")
//	implementation("org.apache.tomcat.embed:tomcat-embed-jasper")
//
//	//kcp
//	implementation(files("lib/CtCli-1.0.6.jar"))
//
//	// spring mobile
//	implementation("org.springframework.mobile:spring-mobile-device:1.1.3.RELEASE")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
