package org.cyk.utility.identifier.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__buildParameterName__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__buildParameterValue__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__buildPathIdentifier__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__buildPath__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__buildQuery__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__build__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__getComponent__;
import static org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl.__setPathByIdentifier__;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.object.dynamic.AbstractObject;
import org.cyk.utility.field.FieldName;
import org.cyk.utility.system.action.SystemAction;
import org.cyk.utility.system.action.SystemActionCreate;
import org.cyk.utility.system.action.SystemActionDelete;
import org.cyk.utility.system.action.SystemActionList;
import org.cyk.utility.system.action.SystemActionRead;
import org.cyk.utility.system.action.SystemActionTree;
import org.cyk.utility.system.action.SystemActionUpdate;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

public class UniformResourceIdentifierHelperUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void getComponent_null(){
		__assertComponents__(null, null, null, null, null, null, null, null, null);
	}
	
	@Test
	public void getComponent_http_user_pass_localhost_8080_mypath_q1v1_f01(){
		__assertComponents__("http://user:pass@localhost:8080/mypath?q1=v1#f01", "http", "user", "pass", "localhost", "8080", "/mypath", "q1=v1", "f01");
	}
	
	@Test
	public void getComponent_http_user_pass_localhost_8080_emptyPath(){
		__assertComponents__("http://user:pass@localhost:8080", "http", "user", "pass", "localhost", "8080", null, null, null);
	}
	
	@Test
	public void getComponent_http_user_pass_localhost_8080_pathWithSlashOnly(){
		__assertComponents__("http://user:pass@localhost:8080/", "http", "user", "pass", "localhost", "8080", "/", null, null);
	}
	
	@Test
	public void getComponent_http_user_pass_localhost_8080_pathWithSlashOnly_questionMark(){
		__assertComponents__("http://user:pass@localhost:8080/?", "http", "user", "pass", "localhost", "8080", "/", "", null);
	}
	
	@Test
	public void buildParameterName_null(){
		assertThat(__buildParameterName__(null)).isEqualTo(null);
	}
	
	@Test
	public void buildParameterName_string_v01(){
		assertThat(__buildParameterName__("p01")).isEqualTo("p01");
	}
	
	@Test
	public void buildParameterName_class(){
		assertThat(__buildParameterName__(Class.class)).isEqualTo("class");
	}
	
	@Test
	public void buildParameterName_class_entity(){
		assertThat(__buildParameterName__(ParameterName.ENTITY_CLASS)).isEqualTo("entityclass");
	}
	
	@Test
	public void buildParameterName_class_entity_instance(){
		assertThat(__buildParameterName__(ParameterName.ENTITY_IDENTIFIER)).isEqualTo("entityidentifier");
	}
	
	@Test
	public void buildParameterName_class_systemAction(){
		assertThat(__buildParameterName__(ParameterName.ACTION_CLASS)).isEqualTo("actionclass");
	}
	
	@Test
	public void buildParameterName_class_systemAction_instance(){
		assertThat(__buildParameterName__(ParameterName.ACTION_IDENTIFIER)).isEqualTo("actionidentifier");
	}
	
	@Test
	public void buildParameterName_class_nextSystemAction(){
		assertThat(__buildParameterName__(ParameterName.NEXT_ACTION_CLASS)).isEqualTo("nextactionclass");
	}
	
	@Test
	public void buildParameterName_class_nextSystemAction_instance(){
		assertThat(__buildParameterName__(ParameterName.NEXT_ACTION_IDENTIFIER)).isEqualTo("nextactionidentifier");
	}
	
	@Test
	public void buildParameterName_class_windowRenderType_instance(){
		assertThat(__buildParameterName__(ParameterName.WINDOW_RENDER_TYPE_CLASS)).isEqualTo("windowrendertypeclass");
	}
	
	@Test
	public void buildParameterName_field_identifier(){
		assertThat(__buildParameterName__(FieldName.IDENTIFIER)).isEqualTo("identifier");
	}
	
	@Test
	public void buildParameterValue__null(){
		assertThat(__buildParameterValue__(null)).isEqualTo(null);
	}
	
	@Test
	public void buildParameterValue__string(){
		assertThat(__buildParameterValue__("v01")).isEqualTo("v01");
	}
	
	@Test
	public void buildParameterValue__class(){
		assertThat(__buildParameterValue__(Class.class)).isEqualTo("class");
	}
	
	@Test
	public void buildParameterValue__class_system_action_create(){
		assertThat(__buildParameterValue__(SystemActionCreate.class)).isEqualTo("create");
	}
	
	@Test
	public void buildParameterValue__class_system_action_tree(){
		assertThat(__buildParameterValue__(SystemActionTree.class)).isEqualTo("tree");
	}
	
	@Test
	public void buildParameterValue__system_action_create_create(){
		assertThat(__buildParameterValue__(__inject__(SystemActionCreate.class))).isEqualTo("create");
	}
	
	@Test
	public void buildParameterValue__system_action_create_custom(){
		assertThat(__buildParameterValue__(__inject__(SystemActionCreate.class).setIdentifier("custom"))).isEqualTo("custom");
	}
	
	@Test
	public void buildParameterValue__system_action_list_list(){
		assertThat(__buildParameterValue__(__inject__(SystemActionList.class))).isEqualTo("list");
	}
	
	@Test
	public void buildParameterValue__system_action_tree_tree(){
		assertThat(__buildParameterValue__(__inject__(SystemActionTree.class))).isEqualTo("tree");
	}
	
	@Test
	public void buildParameterValue__identifier_MyId(){
		assertThat(__buildParameterValue__(__inject__(Klass.class).setIdentifier("MyId"))).isEqualTo("MyId");
	}
	
	/* query */
	
	@Test
	public void buildQuery_null(){
		assertThat(__buildQuery__((Map<Object,List<Object>>)null)).isEqualTo(null);
	}
	
	@Test
	public void buildQuery_classEqc01(){
		Map<Object,List<Object>> map = new LinkedHashMap<>();
		map.put(Class.class, Arrays.asList("c01"));
		assertThat(__buildQuery__(map)).isEqualTo("class=c01");
	}
	
	@Test
	public void buildQuery_systemActionRead_myEntity() {
		SystemAction systemAction = __inject__(SystemActionRead.class);
		systemAction.getEntities(Boolean.TRUE).setElementClass(MyEntity.class);
		systemAction.getEntities(Boolean.TRUE).add(__inject__(MyEntity.class).setIdentifier("123"));
		assertThat(__buildQuery__(systemAction)).isEqualTo("entityclass=myentity&entityidentifier=123&actionclass=read&actionidentifier=read");
	}
	
	/* path */
	
	@Test
	public void buildPathIdentifier_myEntityCreateView() {
		SystemAction systemAction = __inject__(SystemActionCreate.class);
		systemAction.getEntities(Boolean.TRUE).setElementClass(MyEntity.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("myEntityCreateView");
	}
	
	@Test
	public void buildPathIdentifier_myEntityEditView() {
		assertThat(__buildPathIdentifier__(SystemActionCreate.class,MyEntity.class,Boolean.TRUE)).isEqualTo("myEntityEditView");
	}
	
	@Test
	public void buildPathIdentifier_createView() {
		SystemAction systemAction = __inject__(SystemActionCreate.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("__entity__EditView");
	}
	
	@Test
	public void buildPathIdentifier_myEntityUpdateView() {
		SystemAction systemAction = __inject__(SystemActionUpdate.class);
		systemAction.getEntities(Boolean.TRUE).setElementClass(MyEntity.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("myEntityUpdateView");
	}
	
	@Test
	public void buildPathIdentifier_updateView() {
		SystemAction systemAction = __inject__(SystemActionUpdate.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("__entity__EditView");
	}
	
	@Test
	public void buildPathIdentifier_myEntityDeleteView() {
		SystemAction systemAction = __inject__(SystemActionDelete.class);
		systemAction.getEntities(Boolean.TRUE).setElementClass(MyEntity.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("myEntityDeleteView");
	}
	
	@Test
	public void buildPathIdentifier_deleteView() {
		SystemAction systemAction = __inject__(SystemActionDelete.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("__entity__EditView");
	}
	
	@Test
	public void buildPathIdentifier_myEntityListView() {
		SystemAction systemAction = __inject__(SystemActionList.class);
		systemAction.getEntities(Boolean.TRUE).setElementClass(MyEntity.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("myEntityListView");
	}
	
	@Test
	public void buildPathIdentifier_listView() {
		SystemAction systemAction = __inject__(SystemActionList.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("__entity__ListView");
	}
	
	@Test
	public void buildPathIdentifier_treeView() {
		SystemAction systemAction = __inject__(SystemActionTree.class);
		assertThat(__buildPathIdentifier__(systemAction)).isEqualTo("__entity__TreeView");
	}
	
	@Test
	public void buildPath_null(){
		UniformResourceIdentifierHelperImpl.PATH_ROOT = null;
		assertThat(__buildPath__(null,null)).isEqualTo(null);
	}
	
	@Test
	public void buildPath_identifier_i01(){
		__setPathByIdentifier__("i01", "/p01/p02");
		assertThat(__buildPath__("i01",null)).isEqualTo("/p01/p02");
	}
	
	@Test
	public void buildPath_identifier_i01_defaultContext(){
		UniformResourceIdentifierHelperImpl.PATH_ROOT = "mycontext";
		__setPathByIdentifier__("i01", "/p01/p02");
		assertThat(__buildPath__("i01",null)).isEqualTo("/mycontext/p01/p02");
		UniformResourceIdentifierHelperImpl.PATH_ROOT = null;
	}
	
	@Test
	public void buildPath_context_c01(){
		assertThat(__buildPath__(null,"c01")).isEqualTo("/c01");
	}
	
	@Test
	public void buildPath_context_c01_identifier_01(){
		__setPathByIdentifier__("i01", "/folder/sub");
		assertThat(__buildPath__("i01","c01")).isEqualTo("/c01/folder/sub");
	}
	
	@Test
	public void buildPath_context_c01_slash_identifier_01(){
		__setPathByIdentifier__("i01", "/folder/sub");
		assertThat(__buildPath__("i01","c01/")).isEqualTo("/c01/folder/sub");
	}
	
	@Test
	public void buildPath_context_c01_slash_identifier_01_slash(){
		__setPathByIdentifier__("i01", "/folder/sub/");
		assertThat(__buildPath__("i01","c01/")).isEqualTo("/c01/folder/sub/");
	}
	
	@Test
	public void buildPath_systemAction_create_entityClassIsSet(){
		UniformResourceIdentifierHelperImpl.PATHS_MAP.clear();
		__setPathByIdentifier__("myEntityCreateView", "/myentity/create");
		assertThat(__buildPath__(__inject__(SystemActionCreate.class).setEntityClass(MyEntity.class))).isEqualTo("/myentity/create");
	}
	
	@Test
	public void buildPath_systemAction_create_entityClassIsSet_generic(){
		UniformResourceIdentifierHelperImpl.PATH_ROOT = "/mycontext";
		UniformResourceIdentifierHelperImpl.PATHS_MAP.clear();
		__setPathByIdentifier__("__entity__EditView", "/__entity__/edit");
		assertThat(__buildPath__(__inject__(SystemActionCreate.class).setEntityClass(MyEntity.class))).isEqualTo("/mycontext/__entity__/edit");
		UniformResourceIdentifierHelperImpl.PATH_ROOT = null;
	}
	
	@Test
	public void buildPath_systemAction_create_entityClassIsSet_notFound(){
		UniformResourceIdentifierHelperImpl.PATHS_MAP.clear();
		__setPathByIdentifier__("__entity__EditView", "/__entity__/edit");
		assertThat(__buildPath__(__inject__(SystemActionCreate.class).setEntityClass(MyEntity.class))).isEqualTo("/__entity__/edit");
	}
	
	/**/
	
	@Test
	public void build_http_localhost_8080(){
		assertThat(__build__("http", null, null, "localhost", 8080, null, null, null, null)).isEqualTo("http://localhost:8080");
	}
	
	@Test
	public void build_http_localhost(){
		assertThat(__build__("http", null, null, "localhost", null, null, null, null, null)).isEqualTo("http://localhost");
	}
	
	@Test
	public void build_http_localhost_query_a_equal_b(){
		assertThat(__build__("http", null, null, "localhost", null, null, "a=b", null, null)).isEqualTo("http://localhost?a=b");
	}
	
	@Test
	public void build_https_www_google_com(){
		assertThat(__build__("https", null, null, "www.google.com", null, null, null, null, null)).isEqualTo("https://www.google.com");
	}
	
	@Test
	public void build_http_localhost_8080_slash(){
		assertThat(__build__("http", null, null, "localhost", 8080, "/", null, null, null)).isEqualTo("http://localhost:8080/");
	}
	
	@Test
	public void build_http_localhost_8080_folder(){
		assertThat(__build__("http", null, null, "localhost", 8080, "folder", null, null, null)).isEqualTo("http://localhost:8080/folder");
	}
	
	@Test
	public void build_http_localhost_8080_slash_folder(){
		assertThat(__build__("http", null, null, "localhost", 8080, "/folder", null, null, null)).isEqualTo("http://localhost:8080/folder");
	}
	
	@Test
	public void build_http_localhost_8080_app_folder(){
		assertThat(__build__("http", null, null, "localhost", 8080, "app/folder", null, null, null)).isEqualTo("http://localhost:8080/app/folder");
	}
	
	@Test
	public void build_http_localhost_8080_app_slash_folder(){
		assertThat(__build__("http", null, null, "localhost", 8080, "/app/folder", null, null, null)).isEqualTo("http://localhost:8080/app/folder");
	}
	
	@Test
	public void build_ftp_wwwCykDotCom_app_slash_folder(){
		assertThat(__build__("http", null, null, "localhost", 80, "app/folder", null, null, null)).isEqualTo("http://localhost/app/folder");
	}
	
	/**/
	
	private void __assertComponents__(String uniformResourceIdentifier,String expectedScheme,String expectedUser,String expectedPassword,String expectedHost,String expectedPort
			,String expectedPath,String expectedQuery,String expectedFragment) {
		__assertComponent__(uniformResourceIdentifier, Component.SCHEME, expectedScheme);
		__assertComponent__(uniformResourceIdentifier, Component.USER, expectedUser);
		__assertComponent__(uniformResourceIdentifier, Component.PASSWORD, expectedPassword);
		__assertComponent__(uniformResourceIdentifier, Component.HOST, expectedHost);
		__assertComponent__(uniformResourceIdentifier, Component.PORT, expectedPort);
		__assertComponent__(uniformResourceIdentifier, Component.PATH, expectedPath);
		__assertComponent__(uniformResourceIdentifier, Component.QUERY, expectedQuery);
		__assertComponent__(uniformResourceIdentifier, Component.FRAGMENT, expectedFragment);
	}
	
	private void __assertComponent__(String uniformResourceIdentifier,Component component,String expected) {
		assertThat(__getComponent__(component,uniformResourceIdentifier)).as("%s of %s", component,uniformResourceIdentifier).isEqualTo(expected);
	}
	
	public static class Klass extends AbstractObject {
		private static final long serialVersionUID = 1L;
			
	}
	
	private static class MyEntity extends AbstractObject {
		private static final long serialVersionUID = 1L;
		
	}
	
}
