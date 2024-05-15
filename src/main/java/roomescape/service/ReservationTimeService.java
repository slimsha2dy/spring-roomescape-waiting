package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dao.ReservationDao;
import roomescape.dao.ReservationTimeRepository;
import roomescape.dao.dto.AvailableReservationTimeResultInterface;
import roomescape.domain.reservation.ReservationTime;
import roomescape.exception.AlreadyExistsException;
import roomescape.exception.ExistReservationException;
import roomescape.service.dto.input.AvailableReservationTimeInput;
import roomescape.service.dto.input.ReservationTimeInput;
import roomescape.service.dto.output.AvailableReservationTimeOutput;
import roomescape.service.dto.output.ReservationTimeOutput;

import java.util.List;

import static roomescape.exception.ExceptionDomainType.RESERVATION_TIME;


@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationDao reservationDao;

    public ReservationTimeService(final ReservationTimeRepository reservationTimeRepository, final ReservationDao reservationDao) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationDao = reservationDao;
    }

    public ReservationTimeOutput createReservationTime(final ReservationTimeInput input) {
        final ReservationTime reservationTime = input.toReservationTime();

        if (reservationTimeRepository.existsByStartAt(reservationTime.getStartAt())) {
            throw new AlreadyExistsException(RESERVATION_TIME, reservationTime.getStartAtAsString());
        }

        final ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        return ReservationTimeOutput.toOutput(savedReservationTime);
    }

    public List<ReservationTimeOutput> getAllReservationTimes() {
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return ReservationTimeOutput.toOutputs(reservationTimes);
    }

    public List<AvailableReservationTimeOutput> getAvailableTimes(final AvailableReservationTimeInput input) {
        final List<AvailableReservationTimeResultInterface> availableReservationTimeResults =
                reservationTimeRepository.getAvailableReservationTimeByThemeIdAndDate(input.date(), input.themeId());
        return AvailableReservationTimeOutput.toOutputs(availableReservationTimeResults);
    }

    public void deleteReservationTime(final long id) {
        if (reservationDao.isExistByTimeId(id)) {
            throw new ExistReservationException(RESERVATION_TIME, id);
        }
        reservationTimeRepository.deleteById(id);
    }
}
