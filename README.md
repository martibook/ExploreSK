# ExploreSK
This program explores integration possibilities of [Microsoft Semantic Kernel](https://learn.microsoft.com/en-us/semantic-kernel/overview/)

Semantic Kernel is an open-source SDK that lets you easily combine AI services like OpenAI, Azure OpenAI etc.


At the time, Semantic Kernel was under rapid development so that some desirable functions were not ready for us, especially Java version which is left behind C# version a little.
We want a stream-typed output from it, while the non-stream-typed output was made to be the only default option.
Here I **add a class** which produces stream-typed output, and **some extension methods** which make Semantic Kernel point to my class instead of its default class.
The verification was successful, and the approach works!
