/* 
 * ContentTypeNormalizer.java
 * 
 * Copyright (C) 2012 Jonathan Moore
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.chaplib;

import org.apache.http.HeaderElement;
import org.apache.http.message.BasicHeaderValueParser;

public class ContentTypeNormalizer {

    public String normalize(String contentType) {
        HeaderElement elt = BasicHeaderValueParser.parseHeaderElement(contentType, null);
        if (elt.getParameterByName("charset") == null)
            contentType += ";charset=iso-8859-1";
        return contentType;
    }

}
