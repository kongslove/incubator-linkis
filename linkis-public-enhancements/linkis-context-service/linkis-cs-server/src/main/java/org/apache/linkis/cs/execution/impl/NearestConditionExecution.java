/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.linkis.cs.execution.impl;

import org.apache.linkis.cs.common.entity.source.ContextID;
import org.apache.linkis.cs.condition.impl.NearestCondition;
import org.apache.linkis.cs.contextcache.ContextCacheService;
import org.apache.linkis.cs.execution.fetcher.IterateContextCacheFetcher;
import org.apache.linkis.cs.execution.matcher.NearestLogicContextSearchMatcher;
import org.apache.linkis.cs.execution.ruler.NearestContextSearchRuler;

public class NearestConditionExecution extends UnaryLogicConditionExecution {

  public NearestConditionExecution(
      NearestCondition condition, ContextCacheService contextCacheService, ContextID contextID) {
    super(condition, contextCacheService, contextID);
    this.contextSearchMatcher = new NearestLogicContextSearchMatcher(condition);
    this.contextSearchRuler =
        new NearestContextSearchRuler(
            contextSearchMatcher, contextCacheService, contextID, condition);
    this.contextCacheFetcher =
        new IterateContextCacheFetcher(contextCacheService, contextSearchRuler);
  }
}
