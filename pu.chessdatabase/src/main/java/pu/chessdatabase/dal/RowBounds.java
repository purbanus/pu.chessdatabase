package pu.chessdatabase.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright 2009-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

/**
 * @author Clinton Begin
 */
@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class RowBounds
{
public static final int NO_ROW_OFFSET = 0;
public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
public static final RowBounds DEFAULT = new RowBounds();

private int offset;
private int limit;

public RowBounds()
{
	this( NO_ROW_OFFSET, NO_ROW_LIMIT );
}

}
