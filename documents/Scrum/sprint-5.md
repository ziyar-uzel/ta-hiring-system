# Sprint 5 (Holiday Sprint)

Scrum master: Andrew Mereuta

## Tasks Completed

1. **Completed improving authentication**. Previously we were just checking whether the JWT is valid, now we check if the user has permission to some resource.
2. **Refactored implementation of strategy design pattern**. Now, strategy design pattern has been decoupled and refactored, so that the code is read easily and is tested.
3. **Created more tests both unit and integration**. During holidays, we were continuously increasing code coverage.
4. **Created super cool feature to store created contracts for TAs in S3 Bucket which is provided by AWS**. When student is selected to be TA, the contract is generated and sent to his/her email as well as to cloud storage for administration. 
5. **Added functionality to User micro - service**. Lecturers can now rate TAs.
6. **Cleaned code and folders**. Redundant files, folders and commented code were deleted.
7. **Fixed several found bugs**. Bugs we found and solved are outlined in the next section.

## Bugs: Found and Killed

1. **Scenario** : Student creates applies for TA position (creates submission), then retracts his/her submission and tries to create new submission exactly the same as the one destroyed. 
   The system did not allow for this, saying there already exists one.

2. **Scenario** : Lecturer was able to accept the same submission many times and was sending contract to one student many times.

3. **Scenario** : Lecturer wants to get recommendations of TAs based on their rating, but TAs with no recommendations were not displayed. Now they are displayed, but at the bottom.

## Meetings

> We had meetings over the course of this sprint for discussion on tasks we should do and agree on implementation and one meeting to prepare slides for presentation.



### Meeting on 24th December

 > This meeting was to discuss how we are going to distribute work during holidays and to wish each other Merry Christmas and Happy New Year.

#### Feedback obtained

- [x] Distributed work based on previous impact on the project, so that people with less code can write more.
- [x] Wished Merry Christmas and Happy New Year to each other.





### Meeting on 6th January

> This meeting was to make sure we are on track, outline our goals until Sunday and briefly discuss presentation.

#### Feedback obtained

- [x] Finalise all issues we were working on.
- [x] Finish testing of all untested methods.
- [x] Switch to new style for slides of presentation.
- [x] Prepare slides for the presentation before next meeting.





### Meeting on 9th January

> This meeting was for finish merging of branches that were left. We also discussed all aspects of our implementation and made sure it works. We agreed on next meeting and our next goals. 

#### Obtained feedback about our code

- [x] The code is fine.
- [x] Create updated postman collection.

#### Other feedback

- [x] Update the gitlab issues in particular update time estimates and labels.


## Reflection

**Things learned:**
* Test everything before making a merge request
* When reviewing somebody's code, think out of the box:
    * Imagine yuo are the actual user, and you need to do whatever your purpose is
    * Do not test (manually) only the added functionality, check if previous functionally works and how it integrates with new one

**Things to keep in mind for next weeks/projects:**
* Be positive, being polite is the key to success
* Do not be afraid of asking questions
* Try to notify your teammates about any change in plans that can affect your or their workflow
* Have fun while coding and strive to change even code which looks perfect
