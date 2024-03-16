Feature: Search functionality on a web application

  Background:
    Given User is on the home page

	@test
  Scenario: User searches for a product
    When User enters "Sheet1" in the search bar
    And User clicks the search button
    Then Search results for "" are displayed
    

 
  Scenario Outline: User searches for a product
    When User enters "Sheet1" in the search bar
    And User clicks the search button
    Then Search results for "<searchTerm>" are displayed
    

  Examples:
    | searchTerm |
    | Sheet1   	 |
    | lettuce    |
