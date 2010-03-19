/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.orm.interpreter;

import java.util.LinkedList;

import org.guzz.GuzzContextImpl;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.web.context.ExtendedBeanFactory;
import org.guzz.web.context.ExtendedBeanFactoryAware;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 用于生成和管理interpreter
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BusinessInterpreterManager {
	
	private GuzzContextImpl gc ;
	
	private LinkedList intepreters = new LinkedList() ;
	
	/**此时传入的gc是还没有初始化完成的context*/
	public BusinessInterpreterManager(GuzzContextImpl gc){
		this.gc = gc ;
	}
	
	/**
	 * 新建一个interpreter。如果传入的参数不足以构建，返回null
	 * @throws ClassNotFoundException 
	 */
	public BusinessInterpreter newInterpreter(String ghostName, Class intepretClass, Class domainClass) throws ClassNotFoundException{
		if(intepretClass == null && domainClass == null){
			return null ;
		}
		
		BusinessInterpreter bi = null ;
		
		if(intepretClass != null){
			bi = (BusinessInterpreter) BeanCreator.newBeanInstance(intepretClass) ;
		}else{
			bi = new SEBusinessInterpreter() ;
		}
		
		intepreters.addLast(bi) ;
		
		if(bi instanceof GuzzContextAware && gc.isFullStarted()){
			((GuzzContextAware) bi).setGuzzContext(gc) ;
		}
		
		if(bi instanceof ExtendedBeanFactoryAware && gc.getExtendedBeanFactory() != null){
			((ExtendedBeanFactoryAware) bi).setExtendedBeanFactory(gc.getExtendedBeanFactory()) ;
		}
		
		return bi ;
	}
	
	public void onGuzzFullStarted(){
		for(int i = 0 ; i < intepreters.size(); i++){
			BusinessInterpreter bi = (BusinessInterpreter) intepreters.get(i) ;
			
			if(bi instanceof GuzzContextAware){
				((GuzzContextAware) bi).setGuzzContext(gc) ;
			}
		}
	}
	
	public void onExtendedBeanFactorySetted(ExtendedBeanFactory extendedBeanFactory){
		for(int i = 0 ; i < intepreters.size(); i++){
			BusinessInterpreter bi = (BusinessInterpreter) intepreters.get(i) ;
			
			if(bi instanceof ExtendedBeanFactoryAware){
				((ExtendedBeanFactoryAware) bi).setExtendedBeanFactory(gc.getExtendedBeanFactory()) ;
			}
		}
	}
	
	public void shutdown(){
		this.intepreters.clear() ;
	}

}
