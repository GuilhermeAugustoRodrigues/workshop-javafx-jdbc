package model.service;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

import java.util.List;

public class SellerService {

    SellerDao sellerDao = DaoFactory.createSellerDao();

    public List<Seller> findAll() {
        return sellerDao.findAll();
    }

    public void save(Seller seller) {
        if (seller.getId() == null) {
            sellerDao.insert(seller);
        } else {
            sellerDao.update(seller);
        }
    }

    public void delete(Seller seller) {
        sellerDao.deleteById(seller.getId());
    }
}
