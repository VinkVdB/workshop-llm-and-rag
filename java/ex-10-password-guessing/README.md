# Module 10: Password Guessing

## Objective
Learn how to implement a password guessing feature and make it harder for the password to be revealed.

## Contents
- Implementing a chatclient with a password guessing feature.
- Adding a filter to prevent the LLM from revealing the password.
- Making the password guessing process harder.

## Instructions

1. **Implement the ChatClient:**
   - Create a new class `ModuleTenApplication` in `src/main/java/infosupport/be/`.
   - Implement a chatclient with a password guessing feature.
   - End the runner if the user guesses the password correctly.

2. **Add a Password Filter:**
   - Create a new class `PasswordFilter` in `src/main/java/infosupport/be/`.
   - Implement a filter to prevent the LLM from revealing the password.
   - Check the response at the end to verify it does not contain the password.

3. **Make the Password Guessing Process Harder:**
   - Modify the prompt to make it harder for the password to be given.
   - Implement techniques to make prompt injection more difficult.

## Examples

### Example 1: Making Prompt Injection More Difficult
- Use more complex and varied prompts.
- Add random noise or irrelevant information to the prompt.
- Use multiple layers of prompts to obfuscate the password.

### Example 2: Implementing a Password Filter
- Check the response for the presence of the password.
- If the password is detected, modify the response to remove or obfuscate it.
- Log any attempts to reveal the password for further analysis.

## Notes
- Ensure that the password is stored securely and not hardcoded in the code.
- Regularly update the password and the filtering techniques to stay ahead of potential attacks.
