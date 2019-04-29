# Encryption using RSA

* Generate key pair: 
```
java -jar coder.jar -g keypair_name
```

* Encrypt string with key: 
```
java -jar coder.jar -e PUBLIC_KEY ORIGINAL_MSG
java "-Dfile.encoding=UTF8" -jar coder.jar -e PUBLIC_KEY ORIGINAL_MSG
```
* Decrypt string with key: 
```
java -jar coder.jar -d PRIVATE_KEY ENCRYPTED_MSG
```
* Encrypt file with key: 
```
java -jar coder.jar -ef PUBLIC_KEY ORIGINAL_FILE
```
* Decrypt file with key: 
```
java -jar coder.jar -df PRIVATE_KEY ENCRYPTED_FILE
```
* Encrypt file with password: 
```
java -jar coder.jar -ep ORIGINAL_FILE PASSWORD
```
* Decrypt file with password: 
```
java -jar coder.jar -dp ENCRYPTED_FILE PASSWORD
```