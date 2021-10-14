# PDF module

This module allows you to create PDF design templates using more suitable XML syntax
and load them directly from code. From code, you are able to access every single element 
by specific id to set a data to your PDF template.

## TODOs
- [x] Static page with fix positioning
- [ ] Automatic drawing of element to next page when end of page was reach
  - [X] Draw items vertically in linear order
  - [ ] Enable to draw rest items of ListContainer to next page without moving whole element
- [ ] Create TableContainer
- [ ] Create Header and Footer elements
- [ ] Implement wrapping option to Text element
- [ ] Provide builder pattern to create elements for more comfortable usage in java

## Technology stack
Project is creating using Kotlin language with compatibility to Java 11.

|Name|Version|
|---|---|
|Kotlin|1.5.31|
### Libraries
Module implements [Apache PDFBox](https://pdfbox.apache.org/) to create PDF file and 
[JSON-Java](https://github.com/stleary/JSON-java) to handle templates written in XML 
and transform them to code definition.

|Name|Version||
|---|---|---|
|Apache PDFBox|3.0.0-RC1|[📦](https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox "Maven repository")|
|JSON-Java|20210307|[📦](https://mvnrepository.com/artifact/org.json/json "Maven repository")|

## How to design PDF:
There are two possibilities how PDF can be designed. Only by code using prepared 
elements or easily by creating XML template.

### by XML template
XML templates have to be in strict format. Root tag is `<pdf>` where you can define 
your pages. Tag `<pdf>` is necessary to generate PDF file because it represents the
**Pdf** object defined in code that is responsible for generating. Page is only place 
where you can insert your element tags that will be drawn while generating as `<Text>`
in example. All elements and xml tags are described [here](#elements-xml-tags-and-possibilities).

#### Example - fromString()
This is basic implementation. This example creates PDF file called _generated.pdf_ in 
application root folder with one page and **Hello, World!** text.
```java
...
String xml = "<pdf><absolutePage><Text gravity=\"center\" text=\"Hello, World!\"/></absolutePage></pdf>";
Pdf.fromString(xml).save("generated.pdf");
...
```
You can also load XML from file in resource folder or save PDF file to your 
OutputStream for future purposes.

#### Example - fromResource()
tbd

### by Java/Kotlin code only
tbd

## Integration to project
The module is not yet published in any of public repositories. So only way how to implement
the module into your project is to build it by your own and implement as .jar or alternatively 
implement it directly as module to your project.

### as module
Open terminal in your project directory and run this script:
```shell
# Download as git project:
$ git clone <module-pdf-git-url> module-pdf

# or rather as git submodule if you are using git already:
$ git submodule add <module-pdf-git-url> module-pdf
```
If you want to implement module directly to your project repository delete .git metadata 
folder from module:
```shell
$ rm -rf module-pdf/.git

# In case you implement the module as git submodule do also: 
$ git rm --cached module-pdf
$ git rm .gitmodules
```
Now add the module to the project dependencies:
```groovy
// Include the module in the project settings.gradle:
include ":module-pdf"

// and implement the module as dependency in the project build.gradle:
dependencies {
  implementation project(':module-pdf')
  ...
}
```
_[(Learn more about declaring dependencies in gradle here)](https://docs.gradle.org/current/userguide/declaring_dependencies.html)_

If you are using **Maven** as project management and comprehension tool see [documentation](https://maven.apache.org/index.html).

### as .jar
tbd

## Elements, XML tags and possibilities
These are all elements you can use to design your PDF file:

|Class name|XML tag|Description|
|---|---|---|
|[Pdf](#pdf)|`<pdf>`|-|
|[AbsolutePage](#absolutepage)|`<absolutePage>`|-|
|[LinearPage](#linearPage)|`<linearPage>`|-|
|[AbsoluteContainer](#absolutecontainer)|`<absoluteContainer>`|-|
|[LinearContainer](#linearcontainer)|`<linearContainer>`|-|
|[ListContainer](#listcontainer)|`<listContainer>`|-|
|[Text](#text)|`<text>`|-|

### Pdf
tbd
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="2">pageType</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">A4</td>
            <td align="left" valign="middle" rowspan="1">Size of page. Declaration this property on a page overwrites global definition from <code>&lt;pdf&gt;</code> tag.</td>
        </tr>
        <tr><td align="left" valign="middle"> <i>Possible values: A2, A3, A4, A5</i></td></tr>
    </tbody>
</table>

#### XML full definition:
```xml
<pdf pageType="A4">...</pdf>
```

### AbsolutePage
tbd
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="2">pageType</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">A4</td>
            <td align="left" valign="middle" rowspan="1">Size of page. Declaration this property on a page overwrites global definition from <code>&lt;pdf&gt;</code> tag.</td>
        </tr>
        <tr><td align="left" valign="middle"> <i>Possible values: A2, A3, A4, A5</i></td></tr>
    </tbody>
</table>

#### XML full definition:
```xml
<absolutePage pageType="A4">...</absolutePage>
```

### LinearPage
tbd
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="2">pageType</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">A4</td>
            <td align="left" valign="middle" rowspan="1">Size of page. Declaration this property on a page overwrites global definition from <code>&lt;pdf&gt;</code> tag.</td>
        </tr>
        <tr><td align="left" valign="middle"> <i>Possible values: A2, A3, A4, A5</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="1">dynamic</td>
            <td align="center" valign="top" rowspan="1">Boolean</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">false</td>
            <td align="left" valign="middle" rowspan="1">Set to true if the page have to draw items dynamically. It means that new page is automatically created when end of actual page was reached and continue drawing on the new one.</td>
        </tr>
    </tbody>
</table>

#### XML full definition:
```xml
<linearPage 
        pageType="A4"
        dynamic="true">...</linearPage>
```

### AbsoluteContainer
tbd
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="1">id</td>
            <td align="center" valign="top" rowspan="1">String</td>
            <td align="center" valign="top" rowspan="1">Yes</td>
            <td align="center" valign="top" rowspan="1">null</td>
            <td align="left" valign="middle" rowspan="1">Identification of an element by which you can load it in code to fill with data.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="2">gravity</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">left top</td>
            <td align="left" valign="middle" rowspan="1">Relative position of element in parent. It is possible to combine flags with space as separator (eg.: <code>gravity="top center_horizontal"</code>).</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: left, top, bottom, right, center_horizontal, center_vertical, center</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="1">padding</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">All side padding of element. Padding is applied according to gravity (eg.: paddingLeft not applies to element with gravity set to right).</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingLeft</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Left side padding of element. Padding is applied according to gravity (eg.: paddingLeft not applies to element with gravity set to right). Overwrites global padding property for left side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingTop</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Top side padding of element. Padding is applied according to gravity (eg.: paddingTop not applies to element with gravity set to bottom). Overwrites global padding property for top side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingRight</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Right side padding of element. Padding is applied according to gravity (eg.: paddingRight not applies to element with gravity set to left). Overwrites global padding property for right side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingBottom</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Bottom side padding of element. Padding is applied according to gravity (eg.: paddingBottom not applies to element with gravity set to top). Overwrites global padding property for bottom side only</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="2">width</td>
            <td align="center" valign="top" rowspan="2">Enum / Float</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">fill_parent</td>
            <td align="left" valign="middle" rowspan="1">Width of element. Can be set as fix float or relative to maximum possible width in parent or as minimum possible size according to content.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: fill_parent, wrap_content</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="2">height</td>
            <td align="center" valign="top" rowspan="2">Enum / Float</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">fill_parent</td>
            <td align="left" valign="middle" rowspan="1">Height of element. Can be set as fix float or relative to maximum possible height in parent or as minimum possible size according to content.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: fill_parent, wrap_content</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="2">strokeColor</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">black</td>
            <td align="left" valign="middle" rowspan="1">Border color of element.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: black, blue</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="1">strokeWidth</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Border width of element.</td>
        </tr>
    </tbody>
</table>

#### XML full definition:
```xml
<absoluteContainer  
        id="containerId"
        gravity="left top"
        paddingLeft="0"
        paddingTop="0"
        paddingRight="0"
        paddingBottom="0"
        width="fill_parent"
        height="fill_parent"
        strokeColor="black"
        strokeWidth="0">...</absoluteContainer>
```

### LinearContainer
tbd
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="1">id</td>
            <td align="center" valign="top" rowspan="1">String</td>
            <td align="center" valign="top" rowspan="1">Yes</td>
            <td align="center" valign="top" rowspan="1">null</td>
            <td align="left" valign="middle" rowspan="1">Identification of an element by which you can load it in code to fill with data.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="2">gravity</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">left top</td>
            <td align="left" valign="middle" rowspan="1">Relative position of element in parent. It is possible to combine flags with space as separator (eg.: <code>gravity="top center_horizontal"</code>).</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: left, top, bottom, right, center_horizontal, center_vertical, center</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="1">padding</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">All side padding of element. Padding is applied according to gravity (eg.: paddingLeft not applies to element with gravity set to right).</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingLeft</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Left side padding of element. Padding is applied according to gravity (eg.: paddingLeft not applies to element with gravity set to right). Overwrites global padding property for left side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingTop</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Top side padding of element. Padding is applied according to gravity (eg.: paddingTop not applies to element with gravity set to bottom). Overwrites global padding property for top side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingRight</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Right side padding of element. Padding is applied according to gravity (eg.: paddingRight not applies to element with gravity set to left). Overwrites global padding property for right side only.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="1">paddingBottom</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Bottom side padding of element. Padding is applied according to gravity (eg.: paddingBottom not applies to element with gravity set to top). Overwrites global padding property for bottom side only</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="2">width</td>
            <td align="center" valign="top" rowspan="2">Enum / Float</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">fill_parent</td>
            <td align="left" valign="middle" rowspan="1">Width of element. Can be set as fix float or relative to maximum possible width in parent or as minimum possible size according to content.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: fill_parent, wrap_content</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="2">height</td>
            <td align="center" valign="top" rowspan="2">Enum / Float</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">fill_parent</td>
            <td align="left" valign="middle" rowspan="1">Height of element. Can be set as fix float or relative to maximum possible height in parent or as minimum possible size according to content.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: fill_parent, wrap_content</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="2">strokeColor</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">black</td>
            <td align="left" valign="middle" rowspan="1">Border color of element.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: black, blue</i></td></tr>
        <tr>
            <td align="left" valign="top" rowspan="1">strokeWidth</td>
            <td align="center" valign="top" rowspan="1">Float</td>
            <td align="center" valign="top" rowspan="1">No</td>
            <td align="center" valign="top" rowspan="1">0</td>
            <td align="left" valign="middle" rowspan="1">Border width of element.</td>
        </tr>
        <tr>
            <td align="left" valign="top" rowspan="2">orientation</td>
            <td align="center" valign="top" rowspan="2">Enum</td>
            <td align="center" valign="top" rowspan="2">No</td>
            <td align="center" valign="top" rowspan="2">vertical</td>
            <td align="left" valign="middle" rowspan="1">Orientation declares in which way are child elements drawn.</td>
        </tr>
        <tr><td align="left" valign="middle"><i>Possible values: vertical, horizontal</i></td></tr>
    </tbody>
</table>

#### Special properties
<table>
    <thead>
        <tr>
            <th>Property name</th>
            <th>Type</th>
            <th>Nullable</th>
            <th>Default value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="left" valign="top" rowspan="1">weigh</td>
            <td align="center" valign="top" rowspan="1">Integer</td>
            <td align="center" valign="top" rowspan="1">Yes</td>
            <td align="center" valign="top" rowspan="1">null</td>
            <td align="left" valign="middle" rowspan="1">This special property applies to child elements. It represents ratio of size according to other elements. Using on width or height depends on orientation of container. If weigh is defined on child element, actual set size is ignored and will be calculated automatically.</td>
        </tr>
    </tbody>
</table>

#### XML full definition:
```xml
<linearContainer  
        id="containerId"
        gravity="left top"
        paddingLeft="0"
        paddingTop="0"
        paddingRight="0"
        paddingBottom="0"
        width="fill_parent"
        height="fill_parent"
        strokeColor="black"
        strokeWidth="0"
        orientation="vertical">...</linearContainer>
```

#### XML definition with weigh:
```xml
<linearContainer>
  <absoluteContainer weigh="1">...</absoluteContainer>
  <absoluteContainer weigh="2">...</absoluteContainer>
  <absoluteContainer height="100">...</absoluteContainer>
</linearContainer>
```