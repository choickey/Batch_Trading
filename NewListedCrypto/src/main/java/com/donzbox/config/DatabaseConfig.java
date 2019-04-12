package com.donzbox.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MyBatis는 @MapperScan 어노테이션을 이용하여 java <-> xml <-> DB 연결
 * MyBatis를 사용하기 위해서 SqlSessionFactory에 대한 설정을 추가
 */
@Configuration
// @PropertySource 설정시 주의점 : xxx=localMac(O),  xxx="localMac"(X)
@PropertySource(ignoreResourceNotFound = true, value = "classpath:env/${spring.profiles.active}/jdbc.properties")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(value = { "com.donzbox.mapper" })
public class DatabaseConfig {

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		// 설정에서 각 매퍼에 설정된 캐시를 전역적으로 사용할지 말지에 대한 여부
		configuration.setCacheEnabled(false);
		// 생성키에 대한 JDBC 지원을 허용. 지원하는 드라이버가 필요하다. true로 설정하면 생성키를 강제로 생성한다. 일부 드라이버(예를들면, Derby)에서는 이 설정을 무시한다.
		configuration.setUseGeneratedKeys(false);
		// 디폴트 실행자(executor) 설정. SIMPLE 실행자는 특별히 하는 것이 없다. REUSE 실행자는 PreparedStatement를 재사용한다. BATCH 실행자는 구문을 재사용하고 수정을 배치처리한다.
		configuration.setDefaultExecutorType(ExecutorType.REUSE);
		// 중첩구문내 ResultHandler사용을 허용 허용한다면 false로 설정
		configuration.setSafeResultHandlerEnabled(false);
		// 전통적인 데이터베이스 칼럼명 형태인 A_COLUMN을 CamelCase형태의 자바 프로퍼티명 형태인 aColumn으로 자동으로 매핑하도록 함
		configuration.setMapUnderscoreToCamelCase(true);
		// JDBC타입을 파라미터에 제공하지 않을때 null값을 처리한 JDBC타입을 명시한다. 일부 드라이버는 칼럼의 JDBC타입을 정의하도록 요구하지만 대부분은 NULL, VARCHAR 나 OTHER 처럼 일반적인 값을 사용해서 동작한다.
		configuration.setJdbcTypeForNull(JdbcType.NULL);

		//----------------------------------------------------------------------------------------------------------
		// mybatis는 xml로 sql을 관리하고 있는데 보통 조금 바꿀때 마다 서버를 재구동 시켜야 되는 문제 해결법
		// 단, 운영 시스템에 사용은 보장 못합니다~ 개발시에서만 사용
		//----------------------------------------------------------------------------------------------------------
		// RefreshableSqlSessionFactoryBean sessionFactory = new RefreshableSqlSessionFactoryBean();
		//----------------------------------------------------------------------------------------------------------
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean(); // -> 이거 대신 이하의 커스텀 Bean 이용
		sessionFactory.setConfiguration(configuration);
		sessionFactory.setDataSource(dataSource);
		Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*Mapper.xml");
		sessionFactory.setMapperLocations(res);
		
		return sessionFactory.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}