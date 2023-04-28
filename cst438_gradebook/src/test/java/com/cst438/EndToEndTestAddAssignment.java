package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/nicohartojo/Documents/chromedriver";

	public static final String URL = "http://localhost:3000/AddAssignment";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";
	public static final int TEST_COURSE_ID = 99999;
	public static final String TEST_SEMESTER = "Fall";
	public static final int TEST_YEAR = 2023;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {
		
		Course course = new Course();
		course.setCourse_id(TEST_COURSE_ID);
		course.setSemester(TEST_SEMESTER);
		course.setYear(TEST_YEAR);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setTitle(TEST_COURSE_TITLE);

		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(course);
		enrollment.setId(TEST_COURSE_ID);
		enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
		enrollment.setStudentName(TEST_STUDENT_NAME);
		
		courseRepository.save(course);
		enrollmentRepository.save(enrollment);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			
			//locate text box for assignment name
			driver.findElement(By.xpath("//input[@name='assignment_name']")).sendKeys(TEST_ASSIGNMENT_NAME);
			
			// locate date box for editing due date
			
			driver.findElement(By.xpath("//input[@name='due_date']")).sendKeys("05/03/2023");
			
			// locate text box for course id
			driver.findElement(By.xpath("//input[@name='course_id']")).sendKeys(String.valueOf(TEST_COURSE_ID));
			
			Thread.sleep(SLEEP_DURATION);

			/*
			 *  Locate submit button and click
			 */
			driver.findElement(By.xpath("//button[@id='Submit']")).click();
			Thread.sleep(SLEEP_DURATION);

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			Assignment ass = null;
			Iterable<Assignment> assignments = assignmentRepository.findAll();
			for (Assignment a : assignments) {
				if (a.getCourse().getCourse_id() == TEST_COURSE_ID && a.getName() == TEST_ASSIGNMENT_NAME) {
					ass = a;
				}
			}
			enrollmentRepository.delete(enrollment);
			assignmentRepository.delete(ass);
			courseRepository.delete(course);

			driver.quit();
		}

	}
}
