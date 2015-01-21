# Contributing to JAX-RS Linker

## Source formatting

Please make sure [editorconfig](http://editorconfig.org/) is 
properly configured with your text editor/IDE.

## Commit format

Commits shoud shortly describe in English the change and justify 
why it has been done the way it has been done.

### Format: regex-style

```
(Issue#REF )?Verb-imperative action-done
(
Paragraph(s) explaining the changes)*
```

### Constraints

 - Subject must be at most 50-char long.
 - Body must be wrapped within 70 chars.
 - Issue reference is written in upper case
 - first letter of the subject remaining part is in upper case

### Valid examples

`Issue#123 Fix compiler version`

`Fix typo in ProductResource javadoc`
```
Issue#321 Add /imd/lppr/code/{code}

It is currently impossible to serve /imd/lppr/{code} as LPPR codes 
and IDs are impossible to distinguish from each other, thus leading
to a fatal ambiguity in resource resolution on Jersey slide.
```
```
Issue#321 Switch to Java 12

I am back from the future with Java sources.
```

### Invalid examples

`This is a way too long message for a commit header and should be avoided at all costs`

`Issue#489. I should not use any separator after the issue reference`
```
I should always preprend the detailed changes
with an empty line.
```
```
issue#1323 hello world

Both 'issue' and 'hello' should start with an uppercase letter
```
```
Issue#1323 Added some stuff

Enforce imperative ('Add') rather than past ('Added') in the subject
```

