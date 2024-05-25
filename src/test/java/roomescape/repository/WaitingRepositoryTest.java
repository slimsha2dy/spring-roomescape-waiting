package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingWithRank;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.util.DatabaseCleaner;

@SpringBootTest
class WaitingRepositoryTest {

    @Autowired
    WaitingRepository waitingRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.initialize();
    }

    @Test
    @DisplayName("사용자의 예약 대기 목록을 현재 대기 순번과 함께 반환한다.")
    void find_waitings_with_rank_by_member_id() {
        final var theme = themeRepository.save(ThemeFixture.getDomain());
        final var member1 = memberRepository.save(MemberFixture.getDomain());
        final var member2 = memberRepository.save(MemberFixture.getDomain("new2@gmail.com"));
        final var member3 = memberRepository.save(MemberFixture.getDomain("new3@gmail.com"));
        final var time = timeRepository.save(ReservationTimeFixture.getDomain());
        final var date = ReservationDate.from("2025-05-30");
        reservationRepository.save(new Reservation(null, date, time, theme, member1,
                ReservationStatus.COMPLETE));
        waitingRepository.save(new Waiting(null, date, time, theme, member2, LocalTime.now().minusHours(1)));
        waitingRepository.save(new Waiting(null, date, time, theme, member3, LocalTime.now()));

        WaitingWithRank myWaiting = waitingRepository.findWaitingsWithRankByMemberId(member3.getId()).get(0);
        assertThat(myWaiting.getRank())
                .isEqualTo(2L);
    }
}
