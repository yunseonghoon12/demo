package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<User, Long>을 상속하면 기본적인 CRUD 기능은 자동으로 제공된다.
// findByUsername()처럼 메서드 이름만으로 SQL이 자동 생성된다.
// Optional<User>는 조회 결과가 없을 수도 있다는 것을 안전하게 표현하기 위한 반환 타입이다.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
