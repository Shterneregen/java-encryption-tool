# Encryption using Java

* Generate key pair: 
```
java -jar jet.jar -g [KEYPAIR_NAME] [TARGET_FOLDER]
```

## Message encryption (RSA)
* Encrypt a message with key: 
```
java -jar jet.jar -e PUBLIC_KEY ORIGINAL_MSG
java "-Dfile.encoding=UTF8" -jar jet.jar -e PUBLIC_KEY ORIGINAL_MSG
```
* Decrypt a message with key: 
```
java -jar jet.jar -d PRIVATE_KEY ENCRYPTED_MSG
```

## File encryption (AES)

### File encryption with key
* Encrypt a file with key: 
```
java -jar jet.jar -ef PUBLIC_KEY ORIGINAL_FILE
```
* Decrypt a file with key: 
```
java -jar jet.jar -df PRIVATE_KEY ENCRYPTED_FILE
```

### File encryption with password
* Encrypt a file with password: 
```
java -jar jet.jar -ep ORIGINAL_FILE PASSWORD
```
* Decrypt a file with password: 
```
java -jar jet.jar -dp ENCRYPTED_FILE PASSWORD
```